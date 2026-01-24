package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.InvitationEntity
import org.override.atomo.data.local.entity.InvitationResponseEntity
import org.override.atomo.data.remote.dto.InvitationDto
import org.override.atomo.data.remote.dto.InvitationResponseDto
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse
import org.override.atomo.domain.model.ResponseStatus

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

fun InvitationResponseEntity.toDomain(): InvitationResponse = InvitationResponse(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = ResponseStatus.entries.find { it.name.equals(status, ignoreCase = true) } ?: ResponseStatus.PENDING,
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt
)

fun InvitationResponse.toEntity(): InvitationResponseEntity = InvitationResponseEntity(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = status.name.lowercase(),
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt
)

fun InvitationResponseDto.toEntity(): InvitationResponseEntity = InvitationResponseEntity(
    id = id,
    invitationId = invitationId,
    guestName = guestName,
    status = status,
    dietaryNotes = dietaryNotes,
    plusOne = plusOne,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)
