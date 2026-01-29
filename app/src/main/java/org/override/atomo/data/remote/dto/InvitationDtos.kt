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
 * Data Transfer Object for Invitation.
 */
@Serializable
data class InvitationDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("event_name") val eventName: String,
    @SerialName("event_date") val eventDate: String? = null,
    val location: String? = null,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("template_id") val templateId: String = "elegant",
    @SerialName("primary_color") val primaryColor: String = "#000000",
    @SerialName("font_family") val fontFamily: String = "Inter",
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for Invitation Response.
 */
@Serializable
data class InvitationResponseDto(
    val id: String,
    @SerialName("invitation_id") val invitationId: String,
    @SerialName("guest_name") val guestName: String,
    val status: String = "pending",
    @SerialName("dietary_notes") val dietaryNotes: String? = null,
    @SerialName("plus_one") val plusOne: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
