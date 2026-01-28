/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.cv

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill
import org.override.atomo.domain.repository.CvRepository

/**
 * Wrapper for all CV-related use cases.
 *
 * @property getCvs Retrieves all CVs for a user.
 * @property getCv Retrieves a single CV by ID.
 * @property syncCvs Synchronizes CVs from the backend.
 * @property createCv Creates a new CV.
 * @property updateCv Updates an existing CV.
 * @property deleteCv Deletes a CV.
 * @property addEducation Adds education entry.
 * @property addExperience Adds experience entry.
 * @property addSkill Adds a skill.
 */
data class CvUseCases(
    val getCvs: GetCvsUseCase,
    val getCv: GetCvUseCase,
    val syncCvs: SyncCvsUseCase,
    val createCv: CreateCvUseCase,
    val updateCv: UpdateCvUseCase,
    val deleteCv: DeleteCvUseCase,
    val addEducation: AddEducationUseCase,
    val addExperience: AddExperienceUseCase,
    val addSkill: AddSkillUseCase
)

/** Retrieves all CVs for a specific user as a Flow. */
class GetCvsUseCase(private val repository: CvRepository) {
    operator fun invoke(userId: String): Flow<List<Cv>> = repository.getCvsFlow(userId)
}

/** Retrieves a single CV by its ID as a Flow. */
class GetCvUseCase(private val repository: CvRepository) {
    operator fun invoke(cvId: String): Flow<Cv?> = repository.getCvFlow(cvId)
}

/** Synchronizes CVs from the remote server for a user. */
class SyncCvsUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(userId: String): Result<List<Cv>> = repository.syncCvs(userId)
}

/** Creates a new CV. */
class CreateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cv: Cv): Result<Cv> = repository.createCv(cv)
}

/** Updates an existing CV. */
class UpdateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cv: Cv): Result<Cv> = repository.updateCv(cv)
}

/** Deletes a CV by its ID. */
class DeleteCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cvId: String): Result<Unit> = repository.deleteCv(cvId)
}

/** Adds an education entry to a CV. */
class AddEducationUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(education: CvEducation): Result<CvEducation> = repository.addEducation(education)
}

/** Adds an experience entry to a CV. */
class AddExperienceUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(experience: CvExperience): Result<CvExperience> = repository.addExperience(experience)
}

/** Adds a skill to a CV. */
class AddSkillUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(skill: CvSkill): Result<CvSkill> = repository.addSkill(skill)
}
