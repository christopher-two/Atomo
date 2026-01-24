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
import java.util.UUID

class PortfolioViewModel(
    private val portfolioUseCases: PortfolioUseCases
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
        }
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO
            portfolioUseCases.getPortfolios(userId).collect { list ->
                _state.update { it.copy(isLoading = false, portfolios = list) }
            }
        }
    }

    private fun createPortfolio() {
        viewModelScope.launch {
            val newPortfolio = Portfolio(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id",
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