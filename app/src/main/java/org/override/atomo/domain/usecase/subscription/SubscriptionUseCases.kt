/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.subscription

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.repository.SubscriptionRepository

/**
 * Wrapper for Subscription-related use cases.
 *
 * @property getPlans Retrieves available subscription plans.
 * @property syncPlans Synchronizes plans from the backend.
 * @property getSubscription Retrieves the user's active subscription.
 * @property syncSubscription Synchronizes the user's subscription from the backend.
 * @property cancelSubscription Cancels the user's subscription.
 */
data class SubscriptionUseCases(
    val getPlans: GetPlansUseCase,
    val syncPlans: SyncPlansUseCase,
    val getSubscription: GetSubscriptionUseCase,
    val syncSubscription: SyncSubscriptionUseCase,
    val cancelSubscription: CancelSubscriptionUseCase
)

/** Retrieves available subscription plans as a Flow. */
class GetPlansUseCase(private val repository: SubscriptionRepository) {
    operator fun invoke(): Flow<List<Plan>> = repository.getPlansFlow()
}

/** Synchronizes available plans from the server. */
class SyncPlansUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(): Result<List<Plan>> = repository.syncPlans()
}

/** Retrieves the user's current subscription as a Flow. */
class GetSubscriptionUseCase(private val repository: SubscriptionRepository) {
    operator fun invoke(userId: String): Flow<Subscription?> = repository.getSubscriptionFlow(userId)
}

/** Synchronizes the user's subscription from the server. */
class SyncSubscriptionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(userId: String): Result<Subscription?> = repository.syncSubscription(userId)
}

/** Cancels the user's subscription. */
class CancelSubscriptionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.cancelSubscription(userId)
}
