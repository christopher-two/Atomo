package org.override.atomo.feature.dashboard.presentation

import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.Shop

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    
    // Edit actions (navigate to edit screen)
    data class EditMenu(val menuId: String) : DashboardAction
    data class EditPortfolio(val portfolioId: String) : DashboardAction
    data class EditCv(val cvId: String) : DashboardAction
    data class EditShop(val shopId: String) : DashboardAction
    data class EditInvitation(val invitationId: String) : DashboardAction
    
    // Delete confirmation actions
    data class ConfirmDeleteMenu(val menu: Menu) : DashboardAction
    data class ConfirmDeletePortfolio(val portfolio: Portfolio) : DashboardAction
    data class ConfirmDeleteCv(val cv: Cv) : DashboardAction
    data class ConfirmDeleteShop(val shop: Shop) : DashboardAction
    data class ConfirmDeleteInvitation(val invitation: Invitation) : DashboardAction
    
    // Share/Preview actions
    data class ShareMenu(val menuId: String) : DashboardAction
    data class SharePortfolio(val portfolioId: String) : DashboardAction
    data class ShareCv(val cvId: String) : DashboardAction
    data class ShareShop(val shopId: String) : DashboardAction
    data class ShareInvitation(val invitationId: String) : DashboardAction
    
    // Create actions (from FAB or empty state)
    data object CreateMenu : DashboardAction
    data object CreatePortfolio : DashboardAction
    data object CreateCv : DashboardAction
    data object CreateShop : DashboardAction
    data object CreateInvitation : DashboardAction
    
    // Dialog dismiss
    data object DismissDeleteDialog : DashboardAction
    data object ConfirmDelete : DashboardAction
}