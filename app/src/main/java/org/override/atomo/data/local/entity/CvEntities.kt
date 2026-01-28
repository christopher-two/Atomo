/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for CV.
 */
@Entity(
    tableName = "cvs",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class CvEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val professionalSummary: String?,
    val isVisible: Boolean = true,
    val templateId: String = "standard",
    val primaryColor: String = "#000000",
    val fontFamily: String = "Inter",
    val createdAt: Long
)

/**
 * Room Entity for CV Education.
 */
@Entity(
    tableName = "cv_education",
    foreignKeys = [
        ForeignKey(
            entity = CvEntity::class,
            parentColumns = ["id"],
            childColumns = ["cvId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cvId")]
)
data class CvEducationEntity(
    @PrimaryKey
    val id: String,
    val cvId: String,
    val degree: String,
    val institution: String,
    val startDate: Long?,
    val endDate: Long?,
    val isCurrent: Boolean = false,
    val description: String?,
    val sortOrder: Int = 0,
    val createdAt: Long
)

/**
 * Room Entity for CV Experience.
 */
@Entity(
    tableName = "cv_experience",
    foreignKeys = [
        ForeignKey(
            entity = CvEntity::class,
            parentColumns = ["id"],
            childColumns = ["cvId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cvId")]
)
data class CvExperienceEntity(
    @PrimaryKey
    val id: String,
    val cvId: String,
    val role: String,
    val company: String,
    val startDate: Long?,
    val endDate: Long?,
    val isCurrent: Boolean = false,
    val description: String?,
    val sortOrder: Int = 0,
    val createdAt: Long
)

/**
 * Room Entity for CV Skill.
 */
@Entity(
    tableName = "cv_skills",
    foreignKeys = [
        ForeignKey(
            entity = CvEntity::class,
            parentColumns = ["id"],
            childColumns = ["cvId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cvId")]
)
data class CvSkillEntity(
    @PrimaryKey
    val id: String,
    val cvId: String,
    val name: String,
    val proficiency: String?,
    val sortOrder: Int = 0,
    val createdAt: Long
)
