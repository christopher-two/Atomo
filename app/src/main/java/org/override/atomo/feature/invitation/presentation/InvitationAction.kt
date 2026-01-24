package org.override.atomo.feature.invitation.presentation

sealed interface InvitationAction {
    data object CreateInvitation : InvitationAction
    data class DeleteInvitation(val id: String) : InvitationAction
    data class OpenInvitation(val id: String) : InvitationAction
}