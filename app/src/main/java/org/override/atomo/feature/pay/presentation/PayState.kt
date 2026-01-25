package org.override.atomo.feature.pay.presentation

import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription

data class PayState(
    val isLoading: Boolean = true,
    val isOperationLoading: Boolean = false,
    val plans: List<Plan> = emptyList(),
    val currentSubscription: Subscription? = null,
    val currentPlan: Plan? = null,
    val selectedPlan: Plan? = null,
    val showConfirmDialog: Boolean = false,
    val error: String? = null
) {
    val isPremium: Boolean
        get() = currentPlan?.name?.lowercase() != "free"
    
    val currentPlanName: String
        get() = currentPlan?.name ?: "Gratuito"
}