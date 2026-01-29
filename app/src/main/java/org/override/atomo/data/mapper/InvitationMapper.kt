/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.InvitationEntity
import org.override.atomo.data.local.entity.InvitationResponseEntity
import org.override.atomo.data.remote.dto.InvitationDto
import org.override.atomo.data.remote.dto.InvitationResponseDto
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse
import org.override.atomo.domain.model.ResponseStatus

/** Maps InvitationEntity to Invitation domain model. */
fun InvitationEntity.toDomain(): Invitation = Invitation(
    id = id,
    userId = userId,
    eventName = eventName,
    eventDate = eventDate,
    location = location,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps Invitation domain model to InvitationEntity. */
fun Invitation.toEntity(): InvitationEntity = InvitationEntity(
    id = id,
    userId = userId,
    eventName = eventName,
    eventDate = eventDate,
    location = location,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps InvitationDto to InvitationEntity. */
fun InvitationDto.toEntity(): InvitationEntity = InvitationEntity(
    id = id,
    userId = userId,
    eventName = eventName,
    eventDate = eventDate?.let { parseTimestamp(it) },
    location = location,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Invitation domain model to InvitationDto. */
fun Invitation.toDto(): InvitationDto = InvitationDto(
    id = id,
    userId = userId,
    eventName = eventName,
    eventDate = eventDate?.let { java.time.Instant.ofEpochMilli(it).toString() },
    location = location,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily
)

/** Maps InvitationResponseEntity to InvitationResponse domain model. */
fun InvitationResponseEntity.toDomain(): InvitationResponse = InvitationResponse(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = ResponseStatus.entries.find { it.name.equals(status, ignoreCase = true) } ?: ResponseStatus.PENDING,
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt
)

/** Maps InvitationResponse domain model to InvitationResponseEntity. */
fun InvitationResponse.toEntity(): InvitationResponseEntity = InvitationResponseEntity(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = status.name.lowercase(),
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt
)

/** Maps InvitationResponseDto to InvitationResponseEntity. */
fun InvitationResponseDto.toEntity(): InvitationResponseEntity = InvitationResponseEntity(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = status,
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)
