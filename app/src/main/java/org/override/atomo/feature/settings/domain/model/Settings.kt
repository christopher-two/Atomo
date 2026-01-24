package org.override.atomo.feature.settings.domain.model

data class Settings(
    val appearance: AppearanceSettings,
    val notifications: NotificationSettings,
    val privacy: PrivacySettings
)

data class AppearanceSettings(
    val isDarkModeEnabled: Boolean,
    val theme: String, // "auto", "pink", "blue", "green", "purple"
    val isDynamicColorEnabled: Boolean,
    val isSystemThemeEnabled: Boolean
)

data class NotificationSettings(
    val areNotificationsEnabled: Boolean,
    val isNotificationSoundEnabled: Boolean,
    val notificationPriority: Float
)

data class PrivacySettings(
    val isBiometricAuthEnabled: Boolean,
    val isAnalyticsEnabled: Boolean
)
