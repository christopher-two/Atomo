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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.data.local.entity.PortfolioEntity
import org.override.atomo.data.local.entity.PortfolioItemEntity
import org.override.atomo.data.local.entity.ProfileEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PortfolioDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var portfolioDao: PortfolioDao
    private lateinit var profileDao: ProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        portfolioDao = db.portfolioDao()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetPortfolio() = runTest {
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        
        val portfolio = createPortfolio("port_1", "user_1")
        portfolioDao.insertPortfolio(portfolio)
        
        val loaded = portfolioDao.getPortfolio("port_1")
        assertEquals(portfolio, loaded)
    }

    @Test
    fun insertAndGetPortfolioItems() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        
        val portfolio = createPortfolio("port_2", "user_2")
        portfolioDao.insertPortfolio(portfolio)
        
        val items = listOf(
            createItem("item_1", "port_2", "Project A"),
            createItem("item_2", "port_2", "Project B")
        )
        portfolioDao.insertItems(items)
        
        val loaded = portfolioDao.getItems("port_2")
        assertEquals(2, loaded.size)
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

    private fun createPortfolio(id: String, userId: String) = PortfolioEntity(
        id = id,
        userId = userId,
        title = "Portfolio $id",
        description = "Description $id",
        createdAt = System.currentTimeMillis()
    )

    private fun createItem(id: String, portfolioId: String, title: String) = PortfolioItemEntity(
        id = id,
        portfolioId = portfolioId,
        title = title,
        description = "Work on $title",
        imageUrl = null,
        projectUrl = null,
        createdAt = System.currentTimeMillis()
    )
}
