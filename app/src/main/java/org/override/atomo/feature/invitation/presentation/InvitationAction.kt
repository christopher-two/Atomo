/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.invitation.presentation

import org.override.atomo.domain.model.Invitation

/**
 * Represents the intent/actions that can be performed on the Invitation screen.
 */
sealed interface InvitationAction {
    /** Create a new Invitation. */
    data object CreateInvitation : InvitationAction
    
    /** Delete an Invitation by its ID. */
    data class DeleteInvitation(val id: String) : InvitationAction
    
    /** Open an Invitation for viewing/editing. */
    data class OpenInvitation(val id: String) : InvitationAction
    
    /** Navigate to upgrade plan screen. */
    data object UpgradePlan : InvitationAction
    
    // Editor Actions
    /** Toggle between view and edit modes. */
    data object ToggleEditMode : InvitationAction
    
    /** Update the Invitation currently being edited in the state buffer. */
    data class UpdateEditingInvitation(val invitation: Invitation) : InvitationAction
    
    /** Save changes to the current Invitation. */
    data object SaveInvitation : InvitationAction
    
    /** Cancel current edits and revert to original state. */
    data object CancelEdit : InvitationAction
    
    /** Show or hide the preview bottom sheet. */
    data class TogglePreviewSheet(val show: Boolean) : InvitationAction
    
    /** Navigate back or close current view. */
    data object Back : InvitationAction
}