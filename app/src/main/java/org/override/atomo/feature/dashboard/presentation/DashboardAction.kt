package org.override.atomo.feature.dashboard.presentation

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    
    // Service navigation
    data class NavigateToMenu(val menuId: String) : DashboardAction
    data class NavigateToPortfolio(val portfolioId: String) : DashboardAction
    data class NavigateToCv(val cvId: String) : DashboardAction
    data class NavigateToShop(val shopId: String) : DashboardAction
    data class NavigateToInvitation(val invitationId: String) : DashboardAction
    
    // Quick create actions
    data object CreateMenu : DashboardAction
    data object CreatePortfolio : DashboardAction
    data object CreateCv : DashboardAction
    data object CreateShop : DashboardAction
    data object CreateInvitation : DashboardAction
}