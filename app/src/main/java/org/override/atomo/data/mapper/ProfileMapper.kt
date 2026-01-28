/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.mapper

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.override.atomo.data.local.entity.ProfileEntity
import org.override.atomo.data.remote.dto.ProfileDto
import org.override.atomo.domain.model.Profile
import java.time.Instant

private val json = Json { ignoreUnknownKeys = true }

/** Maps ProfileEntity to Profile domain model. */
fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    socialLinks = socialLinks?.let { 
        runCatching { json.decodeFromString<Map<String, String>>(it) }.getOrNull() 
    },
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Maps Profile domain model to ProfileEntity. */
fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    socialLinks = socialLinks?.let { json.encodeToString(kotlinx.serialization.serializer(), it) },
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Maps ProfileDto to ProfileEntity. */
fun ProfileDto.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    socialLinks = socialLinks?.toString(),
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis(),
    updatedAt = updatedAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Profile domain model to ProfileDto. */
fun Profile.toDto(): ProfileDto = ProfileDto(
    id = id,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    socialLinks = socialLinks?.let { 
        buildJsonObject { 
            it.forEach { (key, value) -> put(key, JsonPrimitive(value)) }
        }
    }
)

internal fun parseTimestamp(iso: String): Long = runCatching {
    Instant.parse(iso).toEpochMilli()
}.getOrDefault(System.currentTimeMillis())
