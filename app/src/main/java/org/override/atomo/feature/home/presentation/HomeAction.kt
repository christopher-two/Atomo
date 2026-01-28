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
