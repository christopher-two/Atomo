/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.PortfolioItem

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
