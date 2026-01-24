package org.override.atomo.feature.invitation.presentation

data class InvitationState(
    val isLoading: Boolean = false,
    val invitations: List<org.override.atomo.domain.model.Invitation> = emptyList(),
    val error: String? = null
)