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
 * Room Entity for Invitation.
 */
@Entity(
    tableName = "invitations",
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
data class InvitationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val eventName: String,
    val eventDate: Long?,
    val location: String?,
    val description: String?,
    val isActive: Boolean = true,
    val templateId: String = "elegant",
    val primaryColor: String = "#000000",
    val fontFamily: String = "Inter",
    val createdAt: Long
)

/**
 * Room Entity for Invitation Response.
 */
@Entity(
    tableName = "invitation_responses",
    foreignKeys = [
        ForeignKey(
            entity = InvitationEntity::class,
            parentColumns = ["id"],
            childColumns = ["invitationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invitationId")]
)
data class InvitationResponseEntity(
    @PrimaryKey
    val id: String,
    val invitationId: String,
    val guestName: String,
    val status: String = "pending",
    val dietaryNotes: String?,
    val plusOne: Boolean = false,
    val createdAt: Long
)
