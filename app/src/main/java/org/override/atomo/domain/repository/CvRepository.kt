/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill

/**
 * Repository interface for managing CVs and related data (Experience, Education, Skills).
 */
interface CvRepository {
    /** Retrieves a Flow of all CVs for a specific user. */
    fun getCvsFlow(userId: String): Flow<List<Cv>>

    /** Retrieves a list of all CVs for a specific user (suspend). */
    suspend fun getCvs(userId: String): List<Cv>

    /** Retrieves a single CV by ID (suspend). */
    suspend fun getCv(cvId: String): Cv?

    /** Retrieves a single CV by ID as a Flow. */
    fun getCvFlow(cvId: String): Flow<Cv?>

    /** Synchronizes CVs from the remote data source. */
    suspend fun syncCvs(userId: String): Result<List<Cv>>

    /** Creates a new CV. */
    suspend fun createCv(cv: Cv): Result<Cv>

    /** Updates an existing CV. */
    suspend fun updateCv(cv: Cv): Result<Cv>

    /** Deletes a CV. */
    suspend fun deleteCv(cvId: String): Result<Unit>

    /** Uploads unsynced local changes to remote. */
    suspend fun syncUp(userId: String): Result<Unit>
    
    // Education operations

    /** Adds an education entry to a CV. */
    suspend fun addEducation(education: CvEducation): Result<CvEducation>

    /** Updates an education entry. */
    suspend fun updateEducation(education: CvEducation): Result<CvEducation>

    /** Deletes an education entry. */
    suspend fun deleteEducation(educationId: String): Result<Unit>
    
    // Experience operations

    /** Adds an experience entry to a CV. */
    suspend fun addExperience(experience: CvExperience): Result<CvExperience>

    /** Updates an experience entry. */
    suspend fun updateExperience(experience: CvExperience): Result<CvExperience>

    /** Deletes an experience entry. */
    suspend fun deleteExperience(experienceId: String): Result<Unit>
    
    // Skill operations

    /** Adds a skill to a CV. */
    suspend fun addSkill(skill: CvSkill): Result<CvSkill>

    /** Updates a skill. */
    suspend fun updateSkill(skill: CvSkill): Result<CvSkill>

    /** Deletes a skill. */
    suspend fun deleteSkill(skillId: String): Result<Unit>
}
