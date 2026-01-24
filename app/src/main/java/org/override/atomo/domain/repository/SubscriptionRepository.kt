package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription

interface SubscriptionRepository {
    // Plan operations
    fun getPlansFlow(): Flow<List<Plan>>
    suspend fun getPlans(): List<Plan>
    suspend fun getPlan(planId: String): Plan?
    suspend fun syncPlans(): Result<List<Plan>>
    
    // Subscription operations
    fun getSubscriptionFlow(userId: String): Flow<Subscription?>
    suspend fun getSubscription(userId: String): Subscription?
    suspend fun syncSubscription(userId: String): Result<Subscription?>
    suspend fun createSubscription(subscription: Subscription): Result<Subscription>
    suspend fun cancelSubscription(userId: String): Result<Unit>
}
