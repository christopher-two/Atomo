package org.override.atomo.feature.profile.presentation

import org.override.atomo.domain.model.Profile

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