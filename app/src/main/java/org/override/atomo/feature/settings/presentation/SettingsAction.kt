/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation

/**
 * Represents the intent/actions that can be performed on the Settings screen.
 */
sealed interface SettingsAction {
    /** Toggle dark mode setting. */
    data class ToggleDarkMode(val enabled: Boolean) : SettingsAction
    
    /** Set the specific theme (e.g., "auto", "light", "dark"). */
    data class SetTheme(val theme: String) : SettingsAction
    
    /** Toggle dynamic color support (Material You). */
    data class ToggleDynamicColor(val enabled: Boolean) : SettingsAction
    
    /** Toggle usage of system theme settings. */
    data class ToggleSystemTheme(val enabled: Boolean) : SettingsAction

    /** Toggle all notifications. */
    data class ToggleNotifications(val enabled: Boolean) : SettingsAction
    
    /** Toggle notification sounds. */
    data class ToggleNotificationSound(val enabled: Boolean) : SettingsAction
    
    /** Set notification priority level. */
    data class SetNotificationPriority(val priority: Float) : SettingsAction

    /** Toggle biometric authentication requirement. */
    data class ToggleBiometricAuth(val enabled: Boolean) : SettingsAction
    
    /** Toggle analytics collection. */
    data class ToggleAnalytics(val enabled: Boolean) : SettingsAction

    /** Logout the current user. */
    data object Logout : SettingsAction
    
    /** Navigate to the Pay/Subscription screen. */
    data object NavigateToPay : SettingsAction
    
    /** Navigate back to the previous screen. */
    data object NavigateBack : SettingsAction
}