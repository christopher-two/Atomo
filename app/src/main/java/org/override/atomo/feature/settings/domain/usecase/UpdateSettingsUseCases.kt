/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.domain.usecase

import org.override.atomo.libs.settings.api.SettingsRepository

class UpdateAppearanceUseCase(
    private val repository: SettingsRepository
) {
    suspend fun toggleDarkMode(enabled: Boolean) = repository.setDarkModeEnabled(enabled)
    suspend fun setTheme(theme: String) = repository.setTheme(theme)
    suspend fun toggleDynamicColor(enabled: Boolean) = repository.setDynamicColorEnabled(enabled)
    suspend fun toggleSystemTheme(enabled: Boolean) = repository.setSystemThemeEnabled(enabled)
}

class UpdateNotificationsUseCase(
    private val repository: SettingsRepository
) {
    suspend fun toggleNotifications(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
    suspend fun toggleNotificationSound(enabled: Boolean) = repository.setNotificationSoundEnabled(enabled)
    suspend fun setNotificationPriority(priority: Float) = repository.setNotificationPriority(priority)
}

class UpdatePrivacyUseCase(
    private val repository: SettingsRepository
) {
    suspend fun toggleBiometricAuth(enabled: Boolean) = repository.setBiometricAuthEnabled(enabled)
    suspend fun toggleAnalytics(enabled: Boolean) = repository.setAnalyticsEnabled(enabled)
}
