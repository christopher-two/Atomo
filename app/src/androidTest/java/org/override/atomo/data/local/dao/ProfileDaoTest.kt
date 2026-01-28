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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.data.local.entity.ProfileEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProfileDaoTest {

    private lateinit var profileDao: ProfileDao
    private lateinit var db: AtomoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetProfile() = runTest {
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        val loaded = profileDao.getProfile("user_1")
        assertEquals(profile, loaded)
    }

    @Test
    @Throws(Exception::class)
    fun getProfileFlow() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        val loaded = profileDao.getProfileFlow("user_2").first()
        assertEquals(profile, loaded)
    }

    @Test
    @Throws(Exception::class)
    fun updateProfile() = runTest {
        val profile = createProfile("user_3")
        profileDao.insertProfile(profile)
        
        val updatedProfile = profile.copy(displayName = "Updated Name")
        profileDao.updateProfile(updatedProfile)
        
        val loaded = profileDao.getProfile("user_3")
        assertEquals("Updated Name", loaded?.displayName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteProfile() = runTest {
        val profile = createProfile("user_4")
        profileDao.insertProfile(profile)
        profileDao.deleteProfile(profile)
        
        val loaded = profileDao.getProfile("user_4")
        assertNull(loaded)
    }

    @Test
    @Throws(Exception::class)
    fun deleteProfileById() = runTest {
        val profile = createProfile("user_5")
        profileDao.insertProfile(profile)
        profileDao.deleteProfileById("user_5")
        
        val loaded = profileDao.getProfile("user_5")
        assertNull(loaded)
    }

    private fun createProfile(id: String) = ProfileEntity(
        id = id,
        username = "testuser_$id",
        displayName = "Test User $id",
        avatarUrl = "https://example.com/avatar.png",
        socialLinks = "{}",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
