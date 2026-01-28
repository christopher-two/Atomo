/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.pay.presentation

import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription

/**
 * Represents the UI state for the Pay/Subscription feature.
 *
 * @property isLoading Whether initial data is loading.
 * @property isOperationLoading Whether a purchase/update operation is in progress.
 * @property plans List of available subscription plans.
 * @property currentSubscription The user's current subscription details.
 * @property currentPlan The plan corresponding to the current subscription.
 * @property selectedPlan The plan selected by the user for potential purchase.
 * @property showConfirmDialog Whether to show the confirmation dialog for purchase.
 * @property error Error message if any operation failed.
 */
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