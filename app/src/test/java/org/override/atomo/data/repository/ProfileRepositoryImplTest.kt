/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.repository

import app.cash.turbine.test
import io.github.jan.supabase.SupabaseClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.override.atomo.data.local.dao.ProfileDao
import org.override.atomo.data.local.entity.ProfileEntity
import org.override.atomo.data.manager.SyncManager

class ProfileRepositoryImplTest {

    private lateinit var repository: ProfileRepositoryImpl
    private val profileDao: ProfileDao = mockk()
    private val supabaseClient: SupabaseClient = mockk()
    private val syncManager: SyncManager = mockk()

    @Before
    fun setUp() {
        repository = ProfileRepositoryImpl(profileDao, supabaseClient, syncManager)
    }

    @Test
    fun `getProfileFlow should emit domain profile when dao has data`() = runTest {
        val userId = "user123"
        val entity = ProfileEntity(
            id = userId,
            username = "testuser",
            displayName = "Test User",
            avatarUrl = null,
            socialLinks = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        
        coEvery { profileDao.getProfileFlow(userId) } returns flowOf(entity)

        repository.getProfileFlow(userId).test {
            val result = awaitItem()
            assertEquals(userId, result?.id)
            assertEquals("testuser", result?.username)
            awaitComplete()
        }
    }

    @Test
    fun `getProfile should return domain profile from dao`() = runTest {
        val userId = "user123"
        val entity = ProfileEntity(
            id = userId,
            username = "testuser",
            displayName = "Test User",
            avatarUrl = null,
            socialLinks = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )

        coEvery { profileDao.getProfile(userId) } returns entity

        val result = repository.getProfile(userId)
        
        assertEquals(userId, result?.id)
        coVerify { profileDao.getProfile(userId) }
    }

    @Test
    fun `getProfile should return null when dao is empty`() = runTest {
        val userId = "unknown"
        coEvery { profileDao.getProfile(userId) } returns null

        val result = repository.getProfile(userId)
        
        assertEquals(null, result)
    }
}
