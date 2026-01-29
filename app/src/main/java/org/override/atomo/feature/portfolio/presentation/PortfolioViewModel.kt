/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.libs.session.api.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * ViewModel for managing Portfolio feature state and business logic.
 * Handles CRUD operations, state management, and navigation logic for Portfolios.
 */
class PortfolioViewModel(
    private val portfolioUseCases: PortfolioUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state = _state
        .onStart { loadPortfolios() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PortfolioState(),
        )

    /**
     * Processes user intents/actions.
     *
     * @param action The action to perform.
     */
    fun onAction(action: PortfolioAction) {
        when (action) {
            is PortfolioAction.CreatePortfolio -> createPortfolio()
            is PortfolioAction.DeletePortfolio -> deletePortfolio(action.id)
            is PortfolioAction.OpenPortfolio -> openPortfolio(action.id)
            is PortfolioAction.UpgradePlan -> { /* Handle navigation */ }
            
            // Editor Actions
            PortfolioAction.ToggleEditMode -> toggleEditMode()
            is PortfolioAction.UpdateEditingPortfolio -> updateEditingPortfolio(action.portfolio)
            PortfolioAction.SavePortfolio -> savePortfolio()
            PortfolioAction.CancelEdit -> cancelEdit()
            is PortfolioAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            PortfolioAction.Back -> handleBack()
        }
    }

    private fun handleBack() {
        if (_state.value.isEditing) {
            cancelEdit()
        } else if (_state.value.editingPortfolio != null) {
            // Close detail view
            _state.update { it.copy(editingPortfolio = null, isEditing = false) }
        } else {
            // Navigate back from root
            // rootNavigation.back() // If we had it injected
        }
    }

    private fun openPortfolio(id: String) {
        val portfolio = _state.value.portfolios.find { it.id == id } ?: return
        _state.update { 
            it.copy(
                editingPortfolio = portfolio, 
                isEditing = false 
            ) 
        }
    }

    private fun toggleEditMode() {
        _state.update { state ->
            val isEditing = !state.isEditing
            state.copy(isEditing = isEditing, hasChanges = false)
        }
    }

    private fun updateEditingPortfolio(portfolio: Portfolio) {
        val original = _state.value.portfolios.find { it.id == portfolio.id }
        val hasChanges = portfolio != original
        _state.update { it.copy(editingPortfolio = portfolio, hasChanges = hasChanges) }
    }

    private fun savePortfolio() {
        viewModelScope.launch {
            val portfolio = _state.value.editingPortfolio ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            portfolioUseCases.updatePortfolio(portfolio).onSuccess {
                _state.update { it.copy(isLoading = false, isEditing = false, hasChanges = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun cancelEdit() {
        // Revert to original
        val currentId = _state.value.editingPortfolio?.id ?: return
        val original = _state.value.portfolios.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingPortfolio = original, hasChanges = false) }
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first()
            
            if (userId == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            launch {
                portfolioUseCases.getPortfolios(userId).collect { list ->
                    _state.update { state -> 
                        // specific logic: if we are editing/viewing a portfolio, update it if it changed in background?
                        // Or keep local state?
                        // For View mode, we want live updates.
                        // For Edit mode, we don't want to overwrite user work.
                        val currentId = state.editingPortfolio?.id
                        val updatedEditing = if (currentId != null && !state.isEditing) {
                             list.find { it.id == currentId } ?: state.editingPortfolio
                        } else {
                             state.editingPortfolio
                        }
                        
                        state.copy(portfolios = list, editingPortfolio = updatedEditing) 
                    }
                    checkCreationLimit(userId)
                }
            }
        }
    }
    
    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.PORTFOLIO)
        _state.update { 
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun createPortfolio() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            // Check limits first
            val limitResult = canCreateServiceUseCase(userId, ServiceType.PORTFOLIO)
            if (limitResult !is CanCreateResult.Success) {
                 _state.update { it.copy(isLoading = false) } // Should show error/dialog
                 return@launch
            }

            val newPortfolio = Portfolio(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "My New Portfolio",
                description = "Showcase your best work",
                isVisible = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            
            portfolioUseCases.createPortfolio(newPortfolio).onSuccess {
                 // Auto-open
                 _state.update { it.copy(isLoading = false) } // Flow will update list
                 // We can manually set editingPortfolio here if Flow is slow
                 // But wait for Flow collection to act is safer for consistency?
                 // Let's set it to ensure immediate UI feedback
                 openPortfolio(newPortfolio.id) // Might fail if list not updated yet.
                 // Better:
                 _state.update { it.copy(editingPortfolio = newPortfolio, isEditing = true, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deletePortfolio(id: String) {
        viewModelScope.launch {
            portfolioUseCases.deletePortfolio(id)
            if (_state.value.editingPortfolio?.id == id) {
                _state.update { it.copy(editingPortfolio = null, isEditing = false) }
            }
        }
    }
}