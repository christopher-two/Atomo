/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.CvEducationEntity
import org.override.atomo.data.local.entity.CvEntity
import org.override.atomo.data.local.entity.CvExperienceEntity
import org.override.atomo.data.local.entity.CvSkillEntity
import org.override.atomo.data.remote.dto.CvDto
import org.override.atomo.data.remote.dto.CvEducationDto
import org.override.atomo.data.remote.dto.CvExperienceDto
import org.override.atomo.data.remote.dto.CvSkillDto
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill

// CV mappers

/** Maps CvEntity to Cv domain model. */
fun CvEntity.toDomain(): Cv = Cv(
    id = id,
    userId = userId,
    title = title,
    professionalSummary = professionalSummary,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps Cv domain model to CvEntity. */
fun Cv.toEntity(): CvEntity = CvEntity(
    id = id,
    userId = userId,
    title = title,
    professionalSummary = professionalSummary,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps CvDto to CvEntity. */
fun CvDto.toEntity(): CvEntity = CvEntity(
    id = id,
    userId = userId,
    title = title,
    professionalSummary = professionalSummary,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Cv domain model to CvDto. */
fun Cv.toDto(): CvDto = CvDto(
    id = id,
    userId = userId,
    title = title,
    professionalSummary = professionalSummary,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily
)

// Education mappers

/** Maps CvEducationEntity to CvEducation domain model. */
fun CvEducationEntity.toDomain(): CvEducation = CvEducation(
    id = id,
    cvId = cvId,
    degree = degree,
    institution = institution,
    startDate = startDate,
    endDate = endDate,
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvEducation domain model to CvEducationEntity. */
fun CvEducation.toEntity(): CvEducationEntity = CvEducationEntity(
    id = id,
    cvId = cvId,
    degree = degree,
    institution = institution,
    startDate = startDate,
    endDate = endDate,
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvEducationDto to CvEducationEntity. */
fun CvEducationDto.toEntity(): CvEducationEntity = CvEducationEntity(
    id = id,
    cvId = cvId,
    degree = degree,
    institution = institution,
    startDate = startDate?.let { parseTimestamp(it) },
    endDate = endDate?.let { parseTimestamp(it) },
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

// Experience mappers

/** Maps CvExperienceEntity to CvExperience domain model. */
fun CvExperienceEntity.toDomain(): CvExperience = CvExperience(
    id = id,
    cvId = cvId,
    role = role,
    company = company,
    startDate = startDate,
    endDate = endDate,
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvExperience domain model to CvExperienceEntity. */
fun CvExperience.toEntity(): CvExperienceEntity = CvExperienceEntity(
    id = id,
    cvId = cvId,
    role = role,
    company = company,
    startDate = startDate,
    endDate = endDate,
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvExperienceDto to CvExperienceEntity. */
fun CvExperienceDto.toEntity(): CvExperienceEntity = CvExperienceEntity(
    id = id,
    cvId = cvId,
    role = role,
    company = company,
    startDate = startDate?.let { parseTimestamp(it) },
    endDate = endDate?.let { parseTimestamp(it) },
    isCurrent = isCurrent,
    description = description,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

// Skill mappers

/** Maps CvSkillEntity to CvSkill domain model. */
fun CvSkillEntity.toDomain(): CvSkill = CvSkill(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvSkill domain model to CvSkillEntity. */
fun CvSkill.toEntity(): CvSkillEntity = CvSkillEntity(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps CvSkillDto to CvSkillEntity. */
fun CvSkillDto.toEntity(): CvSkillEntity = CvSkillEntity(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)
