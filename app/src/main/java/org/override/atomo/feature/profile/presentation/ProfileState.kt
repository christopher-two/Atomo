/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation

import org.override.atomo.domain.model.Profile

/**
 * Represents the UI state for the Profile feature.
 *
 * @property isLoading Whether a background operation is in progress.
 * @property error Error message if any operation failed.
 * @property profile The current user profile.
 * @property isEditing Whether the UI is currently in edit mode.
 * @property editUsername The username currently being edited.
 * @property editDisplayName The display name currently being edited.
 * @property editSocialLinks The social links map currently being edited.
 * @property usernameError Error message related to username validation.
 * @property isCheckingUsername Whether username availability is currently being checked.
 * @property isUsernameAvailable Whether the current username is available.
 */
data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: Profile? = null,
    val isEditing: Boolean = false,
    
    // Editable fields
    val editUsername: String = "",
    val editDisplayName: String = "",
    val editSocialLinks: Map<String, String> = emptyMap(),
    
    // Validation
    val usernameError: String? = null,
    val isCheckingUsername: Boolean = false,
    val isUsernameAvailable: Boolean = true
)