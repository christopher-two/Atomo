/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.model.Shop

data class DashboardStatistics(
    val activeServices: Int = 0,
    val totalViews: Int = 0,
    val totalInteractions: Int = 0
)

data class DashboardState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOperationLoading: Boolean = false,
    val error: String? = null,
    val profile: Profile? = null,
    val services: List<ServiceModule> = emptyList(),
    val activeSheet: DashboardSheet? = null,
    val deleteDialog: DeleteDialogState? = null,
    val statistics: DashboardStatistics = DashboardStatistics(),
    val shortcuts: List<DashboardShortcut> = emptyList()
) {
    val hasAnyServices: Boolean
        get() = services.any {
            when(it) {
                is ServiceModule.MenuModule -> it.menus.isNotEmpty()
                is ServiceModule.PortfolioModule -> it.portfolios.isNotEmpty()
                is ServiceModule.CvModule -> it.cvs.isNotEmpty()
                is ServiceModule.ShopModule -> it.shops.isNotEmpty()
                is ServiceModule.InvitationModule -> it.invitations.isNotEmpty()
            }
        }
}

data class DashboardShortcut(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val action: DashboardAction
)

sealed interface DashboardEvent {
    data class ShowSnackbar(val message: String) : DashboardEvent
}

sealed interface DashboardSheet {
    data class EditMenu(val menuId: String) : DashboardSheet
    data class EditPortfolio(val portfolioId: String) : DashboardSheet
    data class EditCv(val cvId: String) : DashboardSheet
    data class EditShop(val shopId: String) : DashboardSheet
    data class EditInvitation(val invitationId: String) : DashboardSheet
    
    // Sub-items (null object = create mode)
    data class EditDish(val dish: Dish?, val menuId: String) : DashboardSheet
    data class EditPortfolioItem(val item: PortfolioItem?, val portfolioId: String) : DashboardSheet
}

sealed interface DeleteDialogState {
    data class DeleteMenu(val menu: Menu) : DeleteDialogState
    data class DeletePortfolio(val portfolio: Portfolio) : DeleteDialogState
    data class DeleteCv(val cv: Cv) : DeleteDialogState
    data class DeleteShop(val shop: Shop) : DeleteDialogState
    data class DeleteInvitation(val invitation: Invitation) : DeleteDialogState
}

sealed interface ServiceModule {
    val isActive: Boolean
    
    data class MenuModule(
        val menus: List<Menu>,
        val totalDishes: Int,
        val recentDishes: List<Dish>,
        override val isActive: Boolean = menus.isNotEmpty()
    ) : ServiceModule
    
    data class PortfolioModule(
        val portfolios: List<Portfolio>,
        val totalItems: Int,
        val recentItems: List<PortfolioItem>,
        override val isActive: Boolean = portfolios.isNotEmpty()
    ) : ServiceModule
    
    data class CvModule(
        val cvs: List<Cv>,
        val totalSkills: Int,
        val totalExperiences: Int,
        override val isActive: Boolean = cvs.isNotEmpty()
    ) : ServiceModule
    
    data class ShopModule(
        val shops: List<Shop>,
        val totalProducts: Int,
        val recentProducts: List<Product>,
        override val isActive: Boolean = shops.isNotEmpty()
    ) : ServiceModule
    
    data class InvitationModule(
        val invitations: List<Invitation>,
        val activeCount: Int,
        val upcomingEvent: Invitation? = null,
        override val isActive: Boolean = invitations.isNotEmpty()
    ) : ServiceModule
}