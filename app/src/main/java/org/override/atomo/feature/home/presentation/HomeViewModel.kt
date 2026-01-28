/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

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
import org.override.atomo.domain.usecase.subscription.GetExistingServicesUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.domain.model.ServiceType
import kotlin.io.path.Path

class HomeViewModel(
    private val homeNavigation: HomeNavigation,
    private val rootNavigation: RootNavigation,
    private val sessionRepository: SessionRepository,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val getExistingServices: GetExistingServicesUseCase,
    private val syncAllServices: SyncAllServicesUseCase
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

            /* Sync subscription data from server */
            subscriptionUseCases.syncPlans()
            val safeUserId = userId ?: return@launch
            subscriptionUseCases.syncSubscription(safeUserId)

            /* Load subscription */
            val subscription = subscriptionUseCases.getSubscription(safeUserId).firstOrNull()
            val plan = subscription?.planId?.let {
                subscriptionUseCases.getPlans().firstOrNull()?.find { it.id == subscription.planId }
            }

            /* Load existing services */
            val existingServices = getExistingServices(safeUserId)

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

                    /* Sync All Services */
                    syncAllServices(userId)
                    
                    /* Also reload subscription data */
                    loadSubscriptionData()

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

                /* Check if service can be created */
                val availableTypes = _state.value.availableServiceTypes
                if (!availableTypes.contains(action.type)) {
                    /* Show upgrade dialog */
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

                /* Switch to the respective tab */
                when (action.type) {
                    ServiceType.DIGITAL_MENU -> homeNavigation.switchTab(AppTab.DIGITAL_MENU)
                    ServiceType.PORTFOLIO -> homeNavigation.switchTab(AppTab.PORTFOLIO)
                    ServiceType.CV -> homeNavigation.switchTab(AppTab.CV)
                    ServiceType.SHOP -> homeNavigation.switchTab(AppTab.SHOP)
                    ServiceType.INVITATION -> homeNavigation.switchTab(AppTab.INVITATION)
                }
                // No need to navTo RouteApp.Create... as the roots are now the tabs.
            }

            HomeAction.DismissUpgradeDialog -> {
                _state.update { it.copy(showUpgradeDialog = false, upgradeDialogMessage = "") }
            }

            HomeAction.NavigateToPay -> {
                homeNavigation.switchTab(AppTab.PAY)
            }

            HomeAction.ToggleMenu -> {
                _state.update { it.copy(isMenuSheetOpen = !it.isMenuSheetOpen) }
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