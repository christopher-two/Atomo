/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.PlanEntity
import org.override.atomo.data.local.entity.SubscriptionEntity

@Dao
interface SubscriptionDao {
    
    // Plan operations
    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY price ASC")
    fun getPlansFlow(): Flow<List<PlanEntity>>
    
    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY price ASC")
    suspend fun getPlans(): List<PlanEntity>
    
    @Query("SELECT * FROM plans WHERE id = :planId")
    suspend fun getPlan(planId: String): PlanEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<PlanEntity>)
    
    @Update
    suspend fun updatePlan(plan: PlanEntity)
    
    @Delete
    suspend fun deletePlan(plan: PlanEntity)
    
    // Subscription operations
    @Query("SELECT * FROM subscriptions WHERE userId = :userId")
    fun getSubscriptionFlow(userId: String): Flow<SubscriptionEntity?>
    
    @Query("SELECT * FROM subscriptions WHERE userId = :userId")
    suspend fun getSubscription(userId: String): SubscriptionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)
    
    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)
    
    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)
    
    @Query("DELETE FROM subscriptions WHERE userId = :userId")
    suspend fun deleteSubscriptionByUser(userId: String)
}
