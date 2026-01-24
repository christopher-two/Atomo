package org.override.atomo.domain.usecase.subscription

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.repository.SubscriptionRepository

data class SubscriptionUseCases(
    val getPlans: GetPlansUseCase,
    val syncPlans: SyncPlansUseCase,
    val getSubscription: GetSubscriptionUseCase,
    val syncSubscription: SyncSubscriptionUseCase,
    val cancelSubscription: CancelSubscriptionUseCase
)

class GetPlansUseCase(private val repository: SubscriptionRepository) {
    operator fun invoke(): Flow<List<Plan>> = repository.getPlansFlow()
}

class SyncPlansUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(): Result<List<Plan>> = repository.syncPlans()
}

class GetSubscriptionUseCase(private val repository: SubscriptionRepository) {
    operator fun invoke(userId: String): Flow<Subscription?> = repository.getSubscriptionFlow(userId)
}

class SyncSubscriptionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(userId: String): Result<Subscription?> = repository.syncSubscription(userId)
}

class CancelSubscriptionUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.cancelSubscription(userId)
}
