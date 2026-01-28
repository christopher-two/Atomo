/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.home.presentation

import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.domain.model.ServiceType

sealed interface HomeAction {
    data object ToggleFab : HomeAction
    data object CollapseFab : HomeAction
    data object Refresh : HomeAction
    data class SwitchTab(val tab: AppTab) : HomeAction
    data object NavigateToSettings : HomeAction
    data class CreateService(val type: ServiceType) : HomeAction
    data object DismissUpgradeDialog : HomeAction
    data object NavigateToPay : HomeAction
    data object ToggleMenu : HomeAction
}
