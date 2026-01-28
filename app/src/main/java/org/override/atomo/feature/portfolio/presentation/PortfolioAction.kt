package org.override.atomo.feature.portfolio.presentation

sealed interface PortfolioAction {
    data object CreatePortfolio : PortfolioAction
    data class DeletePortfolio(val id: String) : PortfolioAction
    data class OpenPortfolio(val id: String) : PortfolioAction // Opens in View Mode
    data object UpgradePlan : PortfolioAction
    
    // Editor Actions
    data object ToggleEditMode : PortfolioAction
    data class UpdateEditingPortfolio(val portfolio: org.override.atomo.domain.model.Portfolio) : PortfolioAction
    data object SavePortfolio : PortfolioAction
    data object CancelEdit : PortfolioAction
    data class TogglePreviewSheet(val show: Boolean) : PortfolioAction
    data object Back : PortfolioAction
}