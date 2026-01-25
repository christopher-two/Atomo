package org.override.atomo.feature.settings.presentation

sealed interface SettingsAction {
    data class ToggleDarkMode(val enabled: Boolean) : SettingsAction
    data class SetTheme(val theme: String) : SettingsAction
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsAction
    data class ToggleSystemTheme(val enabled: Boolean) : SettingsAction

    data class ToggleNotifications(val enabled: Boolean) : SettingsAction
    data class ToggleNotificationSound(val enabled: Boolean) : SettingsAction
    data class SetNotificationPriority(val priority: Float) : SettingsAction

    data class ToggleBiometricAuth(val enabled: Boolean) : SettingsAction
    data class ToggleAnalytics(val enabled: Boolean) : SettingsAction

    data object Logout : SettingsAction
    data object NavigateToPay : SettingsAction
    data object NavigateBack : SettingsAction
}