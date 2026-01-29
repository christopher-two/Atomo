/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

/**
 * ViewModel for managing Shop feature state and business logic.
 * Handles CRUD operations, state management, and navigation logic for Shops.
 */
class ShopViewModel(
    private val shopUseCases: ShopUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ShopState())
    val state = _state
        .onStart { loadShops() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ShopState(),
        )

    /**
     * Processes user intents/actions.
     *
     * @param action The action to perform.
     */
    fun onAction(action: ShopAction) {
        when (action) {
            is ShopAction.CreateShop -> createShop()
            is ShopAction.DeleteShop -> deleteShop(action.id)
            is ShopAction.OpenShop -> openShop(action.id)
            is ShopAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
            
            // Editor Actions
            ShopAction.ToggleEditMode -> toggleEditMode()
            is ShopAction.UpdateEditingShop -> updateEditingShop(action.shop)
            ShopAction.SaveShop -> saveShop()
            ShopAction.CancelEdit -> cancelEdit()
            is ShopAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            ShopAction.Back -> handleBack()
        }
    }

    private fun handleBack() {
        if (_state.value.isEditing) {
            cancelEdit()
        } else if (_state.value.editingShop != null) {
            // Close detail view
            _state.update { it.copy(editingShop = null, isEditing = false) }
        } else {
            // Navigate back from root if needed
        }
    }

    private fun openShop(id: String) {
        val shop = _state.value.shops.find { it.id == id } ?: return
        _state.update { 
            it.copy(
                editingShop = shop, 
                isEditing = false 
            ) 
        }
    }

    private fun toggleEditMode() {
        _state.update { state -> state.copy(isEditing = !state.isEditing, hasChanges = false) }
    }

    private fun updateEditingShop(shop: Shop) {
        val original = _state.value.shops.find { it.id == shop.id }
        val hasChanges = shop != original
        _state.update { it.copy(editingShop = shop, hasChanges = hasChanges) }
    }

    private fun saveShop() {
        viewModelScope.launch {
            val shop = _state.value.editingShop ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            shopUseCases.updateShop(shop).onSuccess {
                _state.update { it.copy(isLoading = false, isEditing = false, hasChanges = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun cancelEdit() {
        val currentId = _state.value.editingShop?.id ?: return
        val original = _state.value.shops.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingShop = original, hasChanges = false) }
    }

    private fun loadShops() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first()
            
             if (userId == null) {
                // Handle not logged in or return
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            launch {
                shopUseCases.getShops(userId).collect { list ->
                     _state.update { state -> 
                         val currentId = state.editingShop?.id
                        val updatedEditing = if (currentId != null && !state.isEditing) {
                             list.find { it.id == currentId } ?: state.editingShop
                        } else {
                             state.editingShop
                        }
                        
                        state.copy(shops = list, editingShop = updatedEditing)
                    }
                    checkCreationLimit(userId)
                }
            }
        }
    }
    
    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.SHOP)
        _state.update { 
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun createShop() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            val result = canCreateServiceUseCase(userId, ServiceType.SHOP)
            if (result !is CanCreateResult.Success) {
                 _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            val newShop = Shop(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "My New Shop",
                description = "My awesome shop",
                isActive = true,
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            
            shopUseCases.createShop(newShop).onSuccess {
                 _state.update { it.copy(editingShop = newShop, isEditing = true, isLoading = false) }
            }.onFailure { error ->
                 _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteShop(id: String) {
        viewModelScope.launch {
            shopUseCases.deleteShop(id)
            if (_state.value.editingShop?.id == id) {
                _state.update { it.copy(editingShop = null, isEditing = false) }
            }
        }
    }
}