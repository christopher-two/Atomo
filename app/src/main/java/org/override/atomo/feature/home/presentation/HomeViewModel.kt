package org.override.atomo.feature.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.subscription.GetExistingServicesUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import kotlin.io.path.Path

class HomeViewModel(
    private val homeNavigation: HomeNavigation,
    private val rootNavigation: RootNavigation,
    private val sessionRepository: SessionRepository,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val getExistingServices: GetExistingServicesUseCase,
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val portfolioUseCases: PortfolioUseCases,
    private val cvUseCases: CvUseCases,
    private val shopUseCases: ShopUseCases,
    private val invitationUseCases: InvitationUseCases
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
                viewModelScope.launch {
                    _state.update { it.copy(isRefreshing = true) }
                    val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch

                    // Sync Services
                    var syncJobs: List<Deferred<Result<List<Any>>>> = emptyList()

                    when (_state.value.currentTab) {
                        AppTab.DASHBOARD -> {
                            syncJobs = listOf(
                                async { menuUseCases.syncMenus(userId) },
                                async { portfolioUseCases.syncPortfolios(userId) },
                                async { cvUseCases.syncCvs(userId) },
                                async { shopUseCases.syncShops(userId) },
                                async { invitationUseCases.syncInvitations(userId) },
                            )
                            loadSubscriptionData()
                        }

                        AppTab.PROFILE -> {
                            profileUseCases.syncProfile(userId)
                        }

                        AppTab.PAY -> {
                            loadSubscriptionData()
                        }
                    }

                    syncJobs.awaitAll()

                    Log.d("HomeViewModel", "Refresh completed")
                    _state.update { it.copy(isRefreshing = false) }
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
                    _state.update {
                        it.copy(
                            showUpgradeDialog = true,
                            upgradeDialogMessage = message
                        )
                    }
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