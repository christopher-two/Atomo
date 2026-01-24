package org.override.atomo.feature.auth.presentation.viewmodel

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null
)