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

    fun onAction(action: PortfolioAction) {
        when (action) {
            is PortfolioAction.CreatePortfolio -> createPortfolio()
            is PortfolioAction.DeletePortfolio -> deletePortfolio(action.id)
            is PortfolioAction.OpenPortfolio -> { /* Handle navigation */ }
            is PortfolioAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
        }
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first()
            
            if (userId == null) {
                // Handle not logged in or return
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            launch {
                portfolioUseCases.getPortfolios(userId).collect { list ->
                    _state.update { it.copy(portfolios = list) }
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
            
            val result = canCreateServiceUseCase(userId, ServiceType.PORTFOLIO)
            if (result !is CanCreateResult.Success) {
                return@launch
            }
            
            val newPortfolio = Portfolio(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "My Porfolio",
                description = "My awesome work",
                isVisible = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            portfolioUseCases.createPortfolio(newPortfolio)
        }
    }

    private fun deletePortfolio(id: String) {
        viewModelScope.launch {
            portfolioUseCases.deletePortfolio(id)
        }
    }
}