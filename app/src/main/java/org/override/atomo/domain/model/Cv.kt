/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.model

/**
 * Represents a Curriculum Vitae (CV) in the system.
 *
 * @property id Unique identifier for the CV.
 * @property userId The ID of the user who owns this CV.
 * @property title The title of the CV (e.g., "Software Engineer Resume").
 * @property professionalSummary A brief summary of the user's professional profile.
 * @property isVisible Whether this CV is publicly visible.
 * @property templateId The ID of the UI template used for this CV.
 * @property primaryColor The primary color hex code for styling.
 * @property fontFamily The font family name used in the CV.
 * @property createdAt Timestamp of creation.
 * @property education List of educational background entries.
 * @property experience List of professional experience entries.
 * @property skills List of skills.
 */
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

/**
 * Represents an educational qualification.
 *
 * @property id Unique identifier.
 * @property cvId The CV this education belongs to.
 * @property degree The degree or certification obtained.
 * @property institution The name of the school or university.
 * @property startDate Start date timestamp.
 * @property endDate End date timestamp (null if current).
 * @property isCurrent Whether the user is currently studying here.
 * @property description Optional details.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 */
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

/**
 * Represents a professional work experience.
 *
 * @property id Unique identifier.
 * @property cvId The CV this experience belongs to.
 * @property role The job title.
 * @property company The company name.
 * @property startDate Start date timestamp.
 * @property endDate End date timestamp.
 * @property isCurrent Whether the user currently works here.
 * @property description Job responsibilities and achievements.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 */
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

/**
 * Represents a professional skill.
 *
 * @property id Unique identifier.
 * @property cvId The CV this skill belongs to.
 * @property name The skill name (e.g., "Kotlin").
 * @property proficiency Optional proficiency level (e.g., "Expert").
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 */
data class CvSkill(
    val id: String,
    val cvId: String,
    val name: String,
    val proficiency: String?,
    val sortOrder: Int,
    val createdAt: Long
)
