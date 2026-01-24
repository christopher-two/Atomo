package org.override.atomo.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation

class HomeViewModel(
    private val homeNavigation: HomeNavigation,
    private val rootNavigation: RootNavigation
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                // Sync initial tab from HomeNavigation
                _state.update { it.copy(currentTab = homeNavigation.currentTab) }
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState()
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ToggleFab -> {
                _state.update { it.copy(isFabExpanded = !it.isFabExpanded) }
            }
            HomeAction.CollapseFab -> {
                _state.update { it.copy(isFabExpanded = false) }
            }
            is HomeAction.SwitchTab -> {
                homeNavigation.switchTab(action.tab)
                _state.update { it.copy(currentTab = action.tab) }
            }
            HomeAction.NavigateToSettings -> {
                rootNavigation.navTo(RouteApp.Settings)
            }
            is HomeAction.CreateService -> {
                _state.update { it.copy(isFabExpanded = false) }
                val route = when (action.type) {
                    ServiceType.DIGITAL_MENU -> RouteApp.CreateDigitalMenu
                    ServiceType.PORTFOLIO -> RouteApp.CreatePortfolio
                    ServiceType.CV -> RouteApp.CreateCV
                    ServiceType.SHOP -> RouteApp.CreateShop
                    ServiceType.INVITATION -> RouteApp.CreateInvitation
                }
                rootNavigation.navTo(route)
            }
        }
    }
}