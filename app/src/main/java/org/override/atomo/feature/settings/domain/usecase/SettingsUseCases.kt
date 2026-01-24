package org.override.atomo.feature.settings.domain.usecase

data class SettingsUseCases(
    val getSettings: GetSettingsUseCase,
    val updateAppearance: UpdateAppearanceUseCase,
    val updateNotifications: UpdateNotificationsUseCase,
    val updatePrivacy: UpdatePrivacyUseCase
)
