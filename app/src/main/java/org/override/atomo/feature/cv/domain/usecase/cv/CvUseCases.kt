/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.cv.domain.usecase.cv

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
