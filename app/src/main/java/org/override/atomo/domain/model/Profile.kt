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
 * Represents a user's profile information.
 *
 * @property id The unique profile ID (usually matches user ID).
 * @property username The unique username handle.
 * @property displayName The display name of the user.
 * @property avatarUrl Optional URL to a profile picture.
 * @property socialLinks Map of social media platform names to URLs.
 * @property createdAt Timestamp of profile creation.
 * @property updatedAt Timestamp of last update.
 */
data class Profile(
    val id: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val socialLinks: Map<String, String>?,
    val createdAt: Long,
    val updatedAt: Long
)
