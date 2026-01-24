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
fun CvSkillEntity.toDomain(): CvSkill = CvSkill(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun CvSkill.toEntity(): CvSkillEntity = CvSkillEntity(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun CvSkillDto.toEntity(): CvSkillEntity = CvSkillEntity(
    id = id,
    cvId = cvId,
    name = name,
    proficiency = proficiency,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)
