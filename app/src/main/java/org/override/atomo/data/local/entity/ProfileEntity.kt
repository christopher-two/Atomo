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
import androidx.room.PrimaryKey

/**
 * Room Entity for User Profile.
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val socialLinks: String?, // JSON string
    val createdAt: Long,
    val updatedAt: Long
)
