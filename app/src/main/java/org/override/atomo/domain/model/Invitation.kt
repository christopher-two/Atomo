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
 * Represents an event invitation.
 *
 * @property id Unique identifier.
 * @property userId The ID of the host.
 * @property eventName The name of the event.
 * @property eventDate The timestamp of the event.
 * @property location The event location.
 * @property description Optional details.
 * @property isActive Whether the invitation is active.
 * @property templateId The UI template ID.
 * @property primaryColor The primary color.
 * @property fontFamily The font family.
 * @property createdAt Creation timestamp.
 * @property responses List of guest responses.
 */
data class Invitation(
    val id: String,
    val userId: String,
    val eventName: String,
    val eventDate: Long?,
    val location: String?,
    val description: String?,
    val isActive: Boolean,
    val templateId: String,
    val primaryColor: String,
    val fontFamily: String,
    val createdAt: Long,
    val responses: List<InvitationResponse> = emptyList()
)

/**
 * Represents a guest's response (RSVP) to an invitation.
 *
 * @property id Unique identifier.
 * @property invitationId The invitation ID.
 * @property guestName The name of the guest.
 * @property status The RSVP status.
 * @property dietaryNotes Optional dietary restrictions or notes.
 * @property plusOne Whether the guest is bringing a plus one.
 * @property createdAt timestamp.
 */
data class InvitationResponse(
    val id: String,
    val invitationId: String,
    val guestName: String,
    val status: ResponseStatus,
    val dietaryNotes: String?,
    val plusOne: Boolean,
    val createdAt: Long
)

/**
 * Enum for RSVP status.
 */
enum class ResponseStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}
