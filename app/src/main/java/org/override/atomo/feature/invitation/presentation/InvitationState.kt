package org.override.atomo.feature.invitation.presentation

import org.override.atomo.domain.model.Invitation

data class InvitationState(
    val isLoading: Boolean = false,
    val invitations: List<Invitation> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingInvitation: Invitation? = null,
    val showPreviewSheet: Boolean = false
)