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
import org.override.atomo.data.local.entity.CvEducationEntity
import org.override.atomo.data.local.entity.CvEntity
import org.override.atomo.data.local.entity.CvExperienceEntity
import org.override.atomo.data.local.entity.CvSkillEntity
import org.override.atomo.data.local.entity.ProfileEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CvDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var cvDao: CvDao
    private lateinit var profileDao: ProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        cvDao = db.cvDao()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetCv() = runTest {
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        
        val cv = createCv("cv_1", "user_1")
        cvDao.insertCv(cv)
        
        val loaded = cvDao.getCv("cv_1")
        assertEquals(cv, loaded)
    }

    @Test
    fun insertAndGetCvDetails() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        
        val cv = createCv("cv_2", "user_2")
        cvDao.insertCv(cv)
        
        val education = createEducation("edu_1", "cv_2", "Bachelor")
        cvDao.insertEducation(education)
        
        val experience = createExperience("exp_1", "cv_2", "Developer")
        cvDao.insertExperience(experience)
        
        val skill = createSkill("skill_1", "cv_2", "Kotlin")
        cvDao.insertSkill(skill)
        
        assertEquals(1, cvDao.getEducation("cv_2").size)
        assertEquals(1, cvDao.getExperience("cv_2").size)
        assertEquals(1, cvDao.getSkills("cv_2").size)
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

    private fun createCv(id: String, userId: String) = CvEntity(
        id = id,
        userId = userId,
        title = "My CV $id",
        professionalSummary = "Summary $id",
        createdAt = System.currentTimeMillis()
    )

    private fun createEducation(id: String, cvId: String, degree: String) = CvEducationEntity(
        id = id,
        cvId = cvId,
        degree = degree,
        institution = "University",
        startDate = null,
        endDate = null,
        description = "Study details",
        createdAt = System.currentTimeMillis()
    )

    private fun createExperience(id: String, cvId: String, role: String) = CvExperienceEntity(
        id = id,
        cvId = cvId,
        role = role,
        company = "Company",
        startDate = null,
        endDate = null,
        description = "Work details",
        createdAt = System.currentTimeMillis()
    )

    private fun createSkill(id: String, cvId: String, name: String) = CvSkillEntity(
        id = id,
        cvId = cvId,
        name = name,
        proficiency = "Expert",
        createdAt = System.currentTimeMillis()
    )
}
