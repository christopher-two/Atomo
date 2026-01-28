/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.DashboardShortcut
import org.override.atomo.feature.dashboard.presentation.ServiceModule

object DashboardHelpers {
    fun generateShortcuts(services: List<ServiceModule>): List<DashboardShortcut> {
        val shortcuts = mutableListOf<DashboardShortcut>()
        
        // Only add shortcuts for ACTIVE services
        
        // MENUS
        services.filterIsInstance<ServiceModule.MenuModule>().firstOrNull()?.let { module ->
            if (module.isActive) {
                // If it has a menu, allow adding dishes or editing
                val menuId = module.menus.first().id
                shortcuts.add(
                    DashboardShortcut(
                        id = "add_dish",
                        title = "Agregar Platillo",
                        icon = Icons.Filled.RestaurantMenu,
                        action = DashboardAction.AddDish(menuId)
                    )
                )
                shortcuts.add(
                    DashboardShortcut(
                        id = "edit_menu",
                        title = "Editar Menú",
                        icon = Icons.Filled.RestaurantMenu,
                        action = DashboardAction.EditMenu(menuId)
                    )
                )
            }
        }
        
        // SHOPS
        services.filterIsInstance<ServiceModule.ShopModule>().firstOrNull()?.let { module ->
             if (module.isActive) {
                 val shopId = module.shops.first().id
                 shortcuts.add(
                    DashboardShortcut(
                        id = "edit_shop",
                        title = "Editar Tienda",
                        icon = Icons.Filled.ShoppingBag,
                        action = DashboardAction.EditShop(shopId)
                    )
                )
             }
        }
        
        // CVS
        services.filterIsInstance<ServiceModule.CvModule>().firstOrNull()?.let { module ->
             if (module.isActive) {
                 val cvId = module.cvs.first().id
                 shortcuts.add(
                    DashboardShortcut(
                        id = "edit_cv",
                        title = "Actualizar CV",
                        icon = Icons.Filled.Description,
                        action = DashboardAction.EditCv(cvId)
                    )
                )
             }
        }
        
        // PORTFOLIO
        services.filterIsInstance<ServiceModule.PortfolioModule>().firstOrNull()?.let { module ->
             if (module.isActive) {
                 val pId = module.portfolios.first().id
                 shortcuts.add(
                    DashboardShortcut(
                        id = "edit_portfolio",
                        title = "Editar Portafolio",
                        icon = Icons.Filled.Description,
                        action = DashboardAction.EditPortfolio(pId)
                    )
                )
             }
        }
        
        // INVITATION
        services.filterIsInstance<ServiceModule.InvitationModule>().firstOrNull()?.let { module ->
             if (module.isActive) {
                 val iId = module.invitations.first().id
                 shortcuts.add(
                    DashboardShortcut(
                        id = "edit_invitation",
                        title = "Editar Invitación",
                        icon = Icons.Filled.Description,
                        action = DashboardAction.EditInvitation(iId)
                    )
                )
             }
        }
        
        return shortcuts
    }
}
