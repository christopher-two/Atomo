package org.override.atomo.feature.home.presentation

import org.override.atomo.feature.navigation.AppTab

data class HomeState(
    val isFabExpanded: Boolean = false,
    val currentTab: AppTab = AppTab.DASHBOARD,
)