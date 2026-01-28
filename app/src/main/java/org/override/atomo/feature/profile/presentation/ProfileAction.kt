/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation

/**
 * Represents the intent/actions that can be performed on the Profile screen.
 */
sealed interface ProfileAction {
    /** Force refresh the profile data from remote. */
    data object Refresh : ProfileAction
    
    /** Enter edit mode to modify profile details. */
    data object EnterEditMode : ProfileAction
    
    /** Cancel current edits and revert to original state. */
    data object CancelEdit : ProfileAction
    
    /** Save changes to the profile. */
    data object SaveProfile : ProfileAction
    
    /** Update the username in the state buffer. */
    data class UpdateUsername(val username: String) : ProfileAction
    
    /** Update the display name in the state buffer. */
    data class UpdateDisplayName(val displayName: String) : ProfileAction
    
    /** Update a social link for a specific platform. */
    data class UpdateSocialLink(val platform: String, val url: String) : ProfileAction
    
    /** Format a social link based on platform rules. */
    data class FormatSocialLink(val platform: String) : ProfileAction
}