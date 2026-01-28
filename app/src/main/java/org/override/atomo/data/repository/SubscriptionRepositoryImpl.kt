/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.SubscriptionDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.PlanDto
import org.override.atomo.data.remote.dto.SubscriptionDto
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.repository.SubscriptionRepository

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
        supabase.from("subscriptions").insert(subscription)
        subscriptionDao.insertSubscription(subscription.toEntity())
        subscription
    }
    
    override suspend fun cancelSubscription(userId: String): Result<Unit> = runCatching {
        supabase.from("subscriptions")
            .update({ set("cancel_at_period_end", true) }) { filter { eq("user_id", userId) } }
        
        subscriptionDao.getSubscription(userId)?.let { current ->
            subscriptionDao.updateSubscription(current.copy(cancelAtPeriodEnd = true))
        }
    }
}
