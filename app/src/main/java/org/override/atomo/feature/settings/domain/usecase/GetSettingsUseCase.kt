package org.override.atomo.feature.settings.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.override.atomo.feature.settings.domain.model.AppearanceSettings
import org.override.atomo.feature.settings.domain.model.NotificationSettings
import org.override.atomo.feature.settings.domain.model.PrivacySettings
import org.override.atomo.feature.settings.domain.model.Settings
import org.override.atomo.libs.settings.api.SettingsRepository

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> {
        val appearanceFlow = combine(
            repository.isDarkModeEnabled(),
            repository.getTheme(),
            repository.isDynamicColorEnabled(),
            repository.isSystemThemeEnabled()
        ) { darkMode, theme, dynamicColor, systemTheme ->
            AppearanceSettings(darkMode, theme, dynamicColor, systemTheme)
        }

        val notificationFlow = combine(
            repository.areNotificationsEnabled(),
            repository.isNotificationSoundEnabled(),
            repository.getNotificationPriority()
        ) { notifs, sound, priority ->
            NotificationSettings(notifs, sound, priority)
        }

        val privacyFlow = combine(
            repository.isBiometricAuthEnabled(),
            repository.isAnalyticsEnabled()
        ) { biometric, analytics ->
            PrivacySettings(biometric, analytics)
        }

        return combine(
            appearanceFlow,
            notificationFlow,
            privacyFlow
        ) { appearance, notifications, privacy ->
            Settings(appearance, notifications, privacy)
        }
    }
}
