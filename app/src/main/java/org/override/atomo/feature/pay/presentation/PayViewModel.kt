package org.override.atomo.feature.pay.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.model.SubscriptionStatus
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

class PayViewModel(
    private val subscriptionUseCases: SubscriptionUseCases,
    private val sessionRepository: SessionRepository,
    private val rootNavigation: RootNavigation
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var userId: String? = null

    private val _state = MutableStateFlow(PayState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PayState()
        )

    init {
        loadData()
    }

    fun onAction(action: PayAction) {
        when (action) {
            PayAction.LoadData -> loadData()
            is PayAction.SelectPlan -> {
                _state.update { it.copy(selectedPlan = action.plan, showConfirmDialog = true) }
            }
            PayAction.ConfirmSubscription -> confirmSubscription()
            PayAction.DismissDialog -> _state.update { it.copy(showConfirmDialog = false, selectedPlan = null) }
            PayAction.NavigateBack -> rootNavigation.back()
        }
    }

    private fun loadData() {
        if (hasLoadedInitialData) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            userId = sessionRepository.getCurrentUserId().firstOrNull()
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }
            
            // Sync plans and subscription from server
            subscriptionUseCases.syncPlans()
            subscriptionUseCases.syncSubscription(userId!!)
            
            // Observe local data
            combine(
                subscriptionUseCases.getPlans(),
                subscriptionUseCases.getSubscription(userId!!)
            ) { plans, subscription ->
                val currentPlan = subscription?.let { sub ->
                    plans.find { it.id == sub.planId }
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        plans = plans.sortedBy { p -> p.price },
                        currentSubscription = subscription,
                        currentPlan = currentPlan
                    )
                }
            }.collect { }
            
            hasLoadedInitialData = true
        }
    }

    private fun confirmSubscription() {
        val selectedPlan = _state.value.selectedPlan ?: return
        val uid = userId ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isOperationLoading = true, showConfirmDialog = false) }
            
            val currentSubscription = _state.value.currentSubscription
            val now = System.currentTimeMillis()
            val oneMonthFromNow = now + (30L * 24 * 60 * 60 * 1000)
            
            // Note: In production, this would go through a payment gateway
            // For now, we just create/update the subscription directly
            val newSubscription = Subscription(
                id = currentSubscription?.id ?: UUID.randomUUID().toString(),
                userId = uid,
                planId = selectedPlan.id,
                status = SubscriptionStatus.ACTIVE,
                currentPeriodStart = now,
                currentPeriodEnd = oneMonthFromNow,
                cancelAtPeriodEnd = false,
                createdAt = currentSubscription?.createdAt ?: now,
                updatedAt = now
            )
            
            // For demo purposes, we update local state
            // In production, this would be handled by a backend after payment
            _state.update {
                it.copy(
                    isOperationLoading = false,
                    currentSubscription = newSubscription,
                    currentPlan = selectedPlan,
                    selectedPlan = null
                )
            }
        }
    }
}