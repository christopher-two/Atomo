package org.override.atomo.libs.settings.api

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Apariencia
    fun isDarkModeEnabled(): Flow<Boolean>
    suspend fun setDarkModeEnabled(enabled: Boolean): Result<Unit>

    fun getTheme(): Flow<String> // auto, pink, blue, green, purple
    suspend fun setTheme(theme: String): Result<Unit>

    // Notificaciones
    fun areNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean): Result<Unit>

    fun isNotificationSoundEnabled(): Flow<Boolean>
    suspend fun setNotificationSoundEnabled(enabled: Boolean): Result<Unit>

    fun getNotificationPriority(): Flow<Float>
    suspend fun setNotificationPriority(priority: Float): Result<Unit>

    // Privacidad
    fun isBiometricAuthEnabled(): Flow<Boolean>
    suspend fun setBiometricAuthEnabled(enabled: Boolean): Result<Unit>

    fun isAnalyticsEnabled(): Flow<Boolean>
    suspend fun setAnalyticsEnabled(enabled: Boolean): Result<Unit>

    fun isDynamicColorEnabled(): Flow<Boolean>
    suspend fun setDynamicColorEnabled(enabled: Boolean)

    fun isSystemThemeEnabled(): Flow<Boolean>
    suspend fun setSystemThemeEnabled(enabled: Boolean): Result<Unit>
}
