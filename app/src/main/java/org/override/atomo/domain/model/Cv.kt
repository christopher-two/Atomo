package org.override.atomo.domain.model

data class Cv(
    val id: String,
    val userId: String,
    val title: String,
    val professionalSummary: String?,
    val isVisible: Boolean,
    val templateId: String,
    val primaryColor: String,
    val fontFamily: String,
    val createdAt: Long,
    val education: List<CvEducation> = emptyList(),
    val experience: List<CvExperience> = emptyList(),
    val skills: List<CvSkill> = emptyList()
)

data class CvEducation(
    val id: String,
    val cvId: String,
    val degree: String,
    val institution: String,
    val startDate: Long?,
    val endDate: Long?,
    val isCurrent: Boolean,
    val description: String?,
    val sortOrder: Int,
    val createdAt: Long
)

data class CvExperience(
    val id: String,
    val cvId: String,
    val role: String,
    val company: String,
    val startDate: Long?,
    val endDate: Long?,
    val isCurrent: Boolean,
    val description: String?,
    val sortOrder: Int,
    val createdAt: Long
)

data class CvSkill(
    val id: String,
    val cvId: String,
    val name: String,
    val proficiency: String?,
    val sortOrder: Int,
    val createdAt: Long
)
