package org.override.atomo.feature.invitation.presentation

import org.override.atomo.domain.model.Invitation

sealed interface InvitationAction {
    data object CreateInvitation : InvitationAction
    data class DeleteInvitation(val id: String) : InvitationAction
    data class OpenInvitation(val id: String) : InvitationAction
    data object UpgradePlan : InvitationAction
    
    // Editor Actions
    data object ToggleEditMode : InvitationAction
    data class UpdateEditingInvitation(val invitation: Invitation) : InvitationAction
    data object SaveInvitation : InvitationAction
    data object CancelEdit : InvitationAction
    data class TogglePreviewSheet(val show: Boolean) : InvitationAction
    data object Back : InvitationAction
}