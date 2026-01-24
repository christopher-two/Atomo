package org.override.atomo.feature.settings.presentation

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null
)