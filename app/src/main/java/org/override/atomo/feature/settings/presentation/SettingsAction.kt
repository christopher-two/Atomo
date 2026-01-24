package org.override.atomo.feature.settings.presentation

sealed interface SettingsAction {
    data object Logout : SettingsAction
}