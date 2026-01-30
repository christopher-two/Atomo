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
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.Shop

sealed interface DeleteDialogState {
    data class DeleteMenu(val menu: Menu) : DeleteDialogState
    data class DeletePortfolio(val portfolio: Portfolio) : DeleteDialogState
    data class DeleteCv(val cv: Cv) : DeleteDialogState
    data class DeleteShop(val shop: Shop) : DeleteDialogState
    data class DeleteInvitation(val invitation: Invitation) : DeleteDialogState
}
