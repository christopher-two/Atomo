/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation.components.base

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.DashboardSheet
import org.override.atomo.feature.dashboard.presentation.DashboardState
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.sheets.EditCvSheet
import org.override.atomo.feature.dashboard.presentation.sheets.EditInvitationSheet
import org.override.atomo.feature.dashboard.presentation.sheets.EditMenuSheet
import org.override.atomo.feature.dashboard.presentation.sheets.EditPortfolioSheet
import org.override.atomo.feature.dashboard.presentation.sheets.EditShopSheet
import org.override.atomo.feature.dashboard.presentation.sheets.items.EditDishSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSheetsHandler(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    state.activeSheet?.let { sheet ->
        when (sheet) {
            is DashboardSheet.EditMenu -> {
                val menu = state.services
                    .filterIsInstance<ServiceModule.MenuModule>()
                    .flatMap { it.menus }
                    .find { it.id == sheet.menuId }
                
                if (menu != null) {
                    EditMenuSheet(
                        menu = menu,
                        onDismiss = { onAction(DashboardAction.DismissSheet) },
                        onSave = { onAction(DashboardAction.UpdateMenu(it)) }
                    )
                }
            }
            is DashboardSheet.EditPortfolio -> {
                val portfolio = state.services
                    .filterIsInstance<ServiceModule.PortfolioModule>()
                    .flatMap { it.portfolios }
                    .find { it.id == sheet.portfolioId }
                
                if (portfolio != null) {
                    EditPortfolioSheet(
                        portfolio = portfolio,
                        onDismiss = { onAction(DashboardAction.DismissSheet) },
                        onSave = { onAction(DashboardAction.UpdatePortfolio(it)) }
                    )
                }
            }
            is DashboardSheet.EditCv -> {
                val cv = state.services
                    .filterIsInstance<ServiceModule.CvModule>()
                    .flatMap { it.cvs }
                    .find { it.id == sheet.cvId }
                
                if (cv != null) {
                    EditCvSheet(
                        cv = cv,
                        onDismiss = { onAction(DashboardAction.DismissSheet) },
                        onSave = { onAction(DashboardAction.UpdateCv(it)) }
                    )
                }
            }
            is DashboardSheet.EditShop -> {
                val shop = state.services
                    .filterIsInstance<ServiceModule.ShopModule>()
                    .flatMap { it.shops }
                    .find { it.id == sheet.shopId }
                
                if (shop != null) {
                    EditShopSheet(
                        shop = shop,
                        onDismiss = { onAction(DashboardAction.DismissSheet) },
                        onSave = { onAction(DashboardAction.UpdateShop(it)) }
                    )
                }
            }
            is DashboardSheet.EditInvitation -> {
                val invitation = state.services
                    .filterIsInstance<ServiceModule.InvitationModule>()
                    .flatMap { it.invitations }
                    .find { it.id == sheet.invitationId }
                
                if (invitation != null) {
                    EditInvitationSheet(
                        invitation = invitation,
                        onDismiss = { onAction(DashboardAction.DismissSheet) },
                        onSave = { onAction(DashboardAction.UpdateInvitation(it)) }
                    )
                }
            }
            // Sub-item sheets
            is DashboardSheet.EditDish -> {
                EditDishSheet(
                    dish = sheet.dish,
                    menuId = sheet.menuId,
                    onDismiss = { onAction(DashboardAction.DismissSheet) },
                    onSave = { onAction(DashboardAction.UpdateDish(it)) }
                )
            }
            is DashboardSheet.EditPortfolioItem -> {
                // TODO: Implement EditPortfolioItemSheet
            }
        }
    }
}
