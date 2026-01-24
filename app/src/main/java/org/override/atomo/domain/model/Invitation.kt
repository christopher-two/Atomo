package org.override.atomo.domain.model

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

data class InvitationResponse(
    val id: String,
    val invitationId: String,
    val guestName: String,
    val status: ResponseStatus,
    val dietaryNotes: String?,
    val plusOne: Boolean,
    val createdAt: Long
)

enum class ResponseStatus {
    PENDING,
    CONFIRMED,
    DECLINED
}
