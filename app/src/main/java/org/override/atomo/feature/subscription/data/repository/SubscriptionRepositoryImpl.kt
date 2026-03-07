/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.subscription.data.repository


import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.atomo.feature.subscription.data.local.dao.SubscriptionDao
import org.override.atomo.feature.subscription.data.mapper.toDomain
import org.override.atomo.feature.subscription.data.mapper.toDto
import org.override.atomo.feature.subscription.data.mapper.toEntity
import org.override.atomo.feature.subscription.data.remote.dto.PlanDto
import org.override.atomo.feature.subscription.data.remote.dto.SubscriptionDto
import org.override.atomo.feature.subscription.domain.model.Plan
import org.override.atomo.feature.subscription.domain.model.Subscription
import org.override.atomo.feature.subscription.domain.repository.SubscriptionRepository

/**
 * Implementation of [SubscriptionRepository] using [SubscriptionDao] and [SupabaseClient].
 */
class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao,
    private val supabase: SupabaseClient
) : SubscriptionRepository {
    
    override fun getPlansFlow(): Flow<List<Plan>> {
        return subscriptionDao.getPlansFlow().map { it.map { p -> p.toDomain() } }
    }
    
    override suspend fun getPlans(): List<Plan> {
        return subscriptionDao.getPlans().map { it.toDomain() }
    }
    
    override suspend fun getPlan(planId: String): Plan? {
        return subscriptionDao.getPlan(planId)?.toDomain()
    }
    
    override suspend fun syncPlans(): Result<List<Plan>> = runCatching {
        val dtos = supabase.from("plans")
            .select { filter { eq("is_active", true) } }
            .decodeList<PlanDto>()
        
        val entities = dtos.map { it.toEntity() }
        subscriptionDao.insertPlans(entities)
        entities.map { it.toDomain() }
    }
    
    override fun getSubscriptionFlow(userId: String): Flow<Subscription?> {
        return subscriptionDao.getSubscriptionFlow(userId).map { it?.toDomain() }
    }
    
    override suspend fun getSubscription(userId: String): Subscription? {
        return subscriptionDao.getSubscription(userId)?.toDomain()
    }
    
    override suspend fun syncSubscription(userId: String): Result<Subscription?> = runCatching {
        val dto = supabase.from("subscriptions")
            .select { filter { eq("user_id", userId) } }
            .decodeSingleOrNull<SubscriptionDto>()
        
        dto?.let {
            val entity = it.toEntity()
            subscriptionDao.insertSubscription(entity)
            entity.toDomain()
        }
    }
    
    override suspend fun createSubscription(subscription: Subscription): Result<Subscription> = runCatching {
        // Persist locally first (optimistic); push to remote via syncSubscriptionUp()
        subscriptionDao.insertSubscription(subscription.toEntity())
        subscription
    }

    /**
     * Pushes the locally stored subscription for [userId] to the remote backend.
     * Must be called after [createSubscription] to complete the remote sync cycle.
     */
    override suspend fun syncSubscriptionUp(userId: String): Result<Unit> = runCatching {
        val entity = subscriptionDao.getSubscription(userId) ?: return@runCatching
        val dto = entity.toDomain().toDto()
        supabase.from("subscriptions").upsert(dto) { onConflict = "id" }
    }

    override suspend fun cancelSubscription(userId: String): Result<Unit> = runCatching {
        supabase.from("subscriptions")
            .update({ set("cancel_at_period_end", true) }) { filter { eq("user_id", userId) } }
        
        subscriptionDao.getSubscription(userId)?.let { current ->
            subscriptionDao.updateSubscription(current.copy(cancelAtPeriodEnd = true))
        }
    }
}
