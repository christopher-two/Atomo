package org.override.atomo.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.domain.usecase.subscription.GetExistingServicesUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository

class HomeViewModel(
    private val homeNavigation: HomeNavigation,
    private val rootNavigation: RootNavigation,
    private val sessionRepository: SessionRepository,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val getExistingServices: GetExistingServicesUseCase
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var userId: String? = null

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState()
        )

    init {
        loadSubscriptionData()
    }

    private fun loadSubscriptionData() {
        viewModelScope.launch {
            userId = sessionRepository.getCurrentUserId().firstOrNull()
            if (userId == null) return@launch
            
            // Sync subscription data from server
            subscriptionUseCases.syncPlans()
            subscriptionUseCases.syncSubscription(userId!!)
            
            // Load subscription
            val subscription = subscriptionUseCases.getSubscription(userId!!).firstOrNull()
            val plan = subscription?.planId?.let { 
                subscriptionUseCases.getPlans().firstOrNull()?.find { it.id == subscription.planId }
            }
            
            // Load existing services
            val existingServices = getExistingServices(userId!!)
            
            _state.update { 
                it.copy(
                    currentTab = homeNavigation.currentTab,
                    currentSubscription = subscription,
                    currentPlan = plan,
                    existingServices = existingServices
                ) 
            }
            hasLoadedInitialData = true
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ToggleFab -> {
                _state.update { it.copy(isFabExpanded = !it.isFabExpanded) }
            }
            HomeAction.CollapseFab -> {
                _state.update { it.copy(isFabExpanded = false) }
            }
            HomeAction.Refresh -> {
                loadSubscriptionData()
                when (state.value.currentTab) {
                    AppTab.DASHBOARD -> rootNavigation.navTo(RouteApp.Home)
                    AppTab.PROFILE -> rootNavigation.navTo(RouteApp.Home)
                    AppTab.PAY -> rootNavigation.navTo(RouteApp.Home)
                }
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
                
                // Check if service can be created
                val availableTypes = _state.value.availableServiceTypes
                if (!availableTypes.contains(action.type)) {
                    // Show upgrade dialog
                    val message = if (_state.value.existingServices[action.type] == true) {
                        "Ya tienes un ${action.type.displayName} creado. Solo puedes tener uno de cada tipo."
                    } else {
                        "Has alcanzado el límite de servicios de tu plan. Actualiza a un plan superior para crear más servicios."
                    }
                    _state.update { it.copy(showUpgradeDialog = true, upgradeDialogMessage = message) }
                    return
                }
                
                val route = when (action.type) {
                    ServiceType.DIGITAL_MENU -> RouteApp.CreateDigitalMenu
                    ServiceType.PORTFOLIO -> RouteApp.CreatePortfolio
                    ServiceType.CV -> RouteApp.CreateCV
                    ServiceType.SHOP -> RouteApp.CreateShop
                    ServiceType.INVITATION -> RouteApp.CreateInvitation
                }
                rootNavigation.navTo(route)
            }
            HomeAction.DismissUpgradeDialog -> {
                _state.update { it.copy(showUpgradeDialog = false, upgradeDialogMessage = "") }
            }

            HomeAction.NavigateToPay -> {
                homeNavigation.switchTab(AppTab.PAY)
            }
        }
    }
}

private val ServiceType.displayName: String
    get() = when (this) {
        ServiceType.DIGITAL_MENU -> "Menú Digital"
        ServiceType.PORTFOLIO -> "Portafolio"
        ServiceType.CV -> "CV"
        ServiceType.SHOP -> "Tienda"
        ServiceType.INVITATION -> "Invitación"
    }