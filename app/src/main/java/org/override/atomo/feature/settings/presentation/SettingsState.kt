package org.override.atomo.feature.settings.presentation

import org.override.atomo.feature.settings.domain.model.AppearanceSettings
import org.override.atomo.feature.settings.domain.model.NotificationSettings
import org.override.atomo.feature.settings.domain.model.PrivacySettings

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