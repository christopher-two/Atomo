/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for CV.
 */
@Serializable
data class CvDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    @SerialName("professional_summary") val professionalSummary: String? = null,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("template_id") val templateId: String = "standard",
    @SerialName("primary_color") val primaryColor: String = "#000000",
    @SerialName("font_family") val fontFamily: String = "Inter",
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for CV Education.
 */
@Serializable
data class CvEducationDto(
    val id: String,
    @SerialName("cv_id") val cvId: String,
    val degree: String,
    val institution: String,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("is_current") val isCurrent: Boolean = false,
    val description: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for CV Experience.
 */
@Serializable
data class CvExperienceDto(
    val id: String,
    @SerialName("cv_id") val cvId: String,
    val role: String,
    val company: String,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    @SerialName("is_current") val isCurrent: Boolean = false,
    val description: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for CV Skill.
 */
@Serializable
data class CvSkillDto(
    val id: String,
    @SerialName("cv_id") val cvId: String,
    val name: String,
    val proficiency: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
