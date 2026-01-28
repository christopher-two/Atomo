/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.data.local.entity.PlanEntity
import org.override.atomo.data.local.entity.ProfileEntity
import org.override.atomo.data.local.entity.SubscriptionEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SubscriptionDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var subscriptionDao: SubscriptionDao
    private lateinit var profileDao: ProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        subscriptionDao = db.subscriptionDao()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetPlan() = runTest {
        val plan = createPlan("plan_pro")
        subscriptionDao.insertPlan(plan)
        
        val loaded = subscriptionDao.getPlan("plan_pro")
        assertEquals(plan, loaded)
    }

    @Test
    fun insertAndGetSubscription() = runTest {
        // Must insert Profile and Plan first due to Foreign Keys
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        
        val plan = createPlan("plan_free")
        subscriptionDao.insertPlan(plan)
        
        val subscription = createSubscription("sub_1", "user_1", "plan_free")
        subscriptionDao.insertSubscription(subscription)
        
        val loaded = subscriptionDao.getSubscription("user_1")
        assertEquals(subscription, loaded)
    }

    @Test
    fun deleteSubscriptionByUser() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        
        val plan = createPlan("plan_basic")
        subscriptionDao.insertPlan(plan)
        
        val subscription = createSubscription("sub_2", "user_2", "plan_basic")
        subscriptionDao.insertSubscription(subscription)
        
        subscriptionDao.deleteSubscriptionByUser("user_2")
        
        val loaded = subscriptionDao.getSubscription("user_2")
        assertNull(loaded)
    }

    private fun createProfile(id: String) = ProfileEntity(
        id = id,
        username = "user_$id",
        displayName = "User $id",
        avatarUrl = null,
        socialLinks = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun createPlan(id: String) = PlanEntity(
        id = id,
        name = "Plan $id",
        description = "Description $id",
        price = 9.99,
        features = "[]",
        createdAt = System.currentTimeMillis()
    )

    private fun createSubscription(id: String, userId: String, planId: String) = SubscriptionEntity(
        id = id,
        userId = userId,
        planId = planId,
        currentPeriodStart = System.currentTimeMillis(),
        currentPeriodEnd = System.currentTimeMillis() + 2592000000L,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
