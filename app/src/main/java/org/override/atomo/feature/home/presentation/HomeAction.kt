package org.override.atomo.feature.home.presentation

import org.override.atomo.feature.navigation.AppTab

sealed interface HomeAction {
    data object ToggleFab : HomeAction
    data object CollapseFab : HomeAction
    data class SwitchTab(val tab: AppTab) : HomeAction
    data object NavigateToSettings : HomeAction
    data class CreateService(val type: ServiceType) : HomeAction
}

enum class ServiceType {
    DIGITAL_MENU,
    PORTFOLIO,
    CV,
    SHOP,
    INVITATION
}