package org.override.atomo.feature.auth.presentation

data class AuthState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)