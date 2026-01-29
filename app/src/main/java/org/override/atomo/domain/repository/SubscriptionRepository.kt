/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription

/**
 * Repository interface for managing Subscriptions and Plans.
 */
interface SubscriptionRepository {
    // Plan operations

    /** Retrieves available plans as a Flow. */
    fun getPlansFlow(): Flow<List<Plan>>

    /** Retrieves available plans (suspend). */
    suspend fun getPlans(): List<Plan>

    /** Retrieves a specific plan by ID. */
    suspend fun getPlan(planId: String): Plan?

    /** Synchronizes available plans from the remote data source. */
    suspend fun syncPlans(): Result<List<Plan>>
    
    // Subscription operations

    /** Retrieves the user's subscription as a Flow. */
    fun getSubscriptionFlow(userId: String): Flow<Subscription?>

    /** Retrieves the user's subscription (suspend). */
    suspend fun getSubscription(userId: String): Subscription?

    /** Synchronizes the user's subscription from the remote source. */
    suspend fun syncSubscription(userId: String): Result<Subscription?>

    /** Creates or upgrades a subscription. */
    suspend fun createSubscription(subscription: Subscription): Result<Subscription>

    /** Cancels the user's subscription. */
    suspend fun cancelSubscription(userId: String): Result<Unit>
}
