package org.override.atomo.feature.profile.presentation

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: org.override.atomo.domain.model.Profile? = null,
    val displayName: String = "",
    val error: String? = null
)