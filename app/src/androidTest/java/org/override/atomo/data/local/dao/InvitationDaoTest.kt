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
import org.override.atomo.data.local.entity.InvitationEntity
import org.override.atomo.data.local.entity.InvitationResponseEntity
import org.override.atomo.data.local.entity.ProfileEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class InvitationDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var invitationDao: InvitationDao
    private lateinit var profileDao: ProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        invitationDao = db.invitationDao()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetInvitation() = runTest {
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        
        val invitation = createInvitation("inv_1", "user_1", "Wedding")
        invitationDao.insertInvitation(invitation)
        
        val loaded = invitationDao.getInvitation("inv_1")
        assertEquals(invitation, loaded)
    }

    @Test
    fun insertAndGetResponses() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        
        val invitation = createInvitation("inv_2", "user_2", "Birthday")
        invitationDao.insertInvitation(invitation)
        
        val responses = listOf(
            createResponse("res_1", "inv_2", "Alice", "confirmed"),
            createResponse("res_2", "inv_2", "Bob", "pending")
        )
        invitationDao.insertResponses(responses)
        
        val confirmedCount = invitationDao.getConfirmedCount("inv_2")
        assertEquals(1, confirmedCount)
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

    private fun createInvitation(id: String, userId: String, event: String) = InvitationEntity(
        id = id,
        userId = userId,
        eventName = event,
        eventDate = null,
        location = "Venue",
        description = "Join us!",
        createdAt = System.currentTimeMillis()
    )

    private fun createResponse(id: String, invitationId: String, guest: String, status: String) = InvitationResponseEntity(
        id = id,
        invitationId = invitationId,
        guestName = guest,
        status = status,
        dietaryNotes = null,
        createdAt = System.currentTimeMillis()
    )
}
