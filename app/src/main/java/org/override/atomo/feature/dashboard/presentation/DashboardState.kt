/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import org.override.atomo.domain.model.Profile

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