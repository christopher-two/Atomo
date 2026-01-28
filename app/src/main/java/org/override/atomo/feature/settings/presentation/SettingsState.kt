/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation

import org.override.atomo.feature.settings.domain.model.AppearanceSettings
import org.override.atomo.feature.settings.domain.model.NotificationSettings
import org.override.atomo.feature.settings.domain.model.PrivacySettings

/**
 * Represents the UI state for the Settings feature.
 *
 * @property appearance Configuration for app appearance (theme, dark mode, etc.).
 * @property notifications Configuration for notifications.
 * @property privacy Configuration for privacy (biometrics, analytics).
 * @property isLoading Whether settings are currently loading.
 */
data class SettingsState(
    val appearance: AppearanceSettings = AppearanceSettings(
        isDarkModeEnabled = false,
        theme = "auto",
        isDynamicColorEnabled = false,
        isSystemThemeEnabled = false
    ),
    val notifications: NotificationSettings = NotificationSettings(
        areNotificationsEnabled = true,
        isNotificationSoundEnabled = true,
        notificationPriority = 0f
    ),
    val privacy: PrivacySettings = PrivacySettings(
        isBiometricAuthEnabled = false,
        isAnalyticsEnabled = true
    ),
    val isLoading: Boolean = false
)