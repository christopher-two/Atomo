package org.override.atomo.feature.auth.presentation.viewmodel

sealed interface AuthAction {
    data object ContinueWithGoogle : AuthAction
    data class OpenUrl(val url: String) : AuthAction
}