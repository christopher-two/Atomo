package org.override.atomo.feature.portfolio.presentation

sealed interface PortfolioAction {
    data object CreatePortfolio : PortfolioAction
    data class DeletePortfolio(val id: String) : PortfolioAction
    data class OpenPortfolio(val id: String) : PortfolioAction
}