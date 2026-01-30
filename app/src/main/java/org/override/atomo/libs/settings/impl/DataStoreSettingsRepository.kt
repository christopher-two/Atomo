/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.settings.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.atomo.libs.settings.api.SettingsRepository

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val THEME_KEY = stringPreferencesKey("theme")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_SOUND_KEY = booleanPreferencesKey("notification_sound")
        val NOTIFICATION_PRIORITY_KEY = floatPreferencesKey("notification_priority")
        val BIOMETRIC_AUTH_KEY = booleanPreferencesKey("biometric_auth")
        val ANALYTICS_KEY = booleanPreferencesKey("analytics")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        val SYSTEM_THEME_KEY = booleanPreferencesKey("system_theme")
    }

    // Helpers
    private fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    private suspend fun <T> setValue(key: Preferences.Key<T>, value: T): Result<Unit> {
        return try {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Implementation

    override fun isDarkModeEnabled(): Flow<Boolean> = getValue(
        DARK_MODE_KEY,
        false
    ) // Default light? User didn't specify, assuming system default logic isn't here yet, but boolean defaults to false.

    override suspend fun setDarkModeEnabled(enabled: Boolean) = setValue(DARK_MODE_KEY, enabled)

    override fun getTheme(): Flow<String> = getValue(THEME_KEY, "auto")
    override suspend fun setTheme(theme: String) = setValue(THEME_KEY, theme)

    override fun areNotificationsEnabled(): Flow<Boolean> =
        getValue(NOTIFICATIONS_ENABLED_KEY, true)

    override suspend fun setNotificationsEnabled(enabled: Boolean) =
        setValue(NOTIFICATIONS_ENABLED_KEY, enabled)

    override fun isNotificationSoundEnabled(): Flow<Boolean> =
        getValue(NOTIFICATION_SOUND_KEY, true)

    override suspend fun setNotificationSoundEnabled(enabled: Boolean) =
        setValue(NOTIFICATION_SOUND_KEY, enabled)

    override fun getNotificationPriority(): Flow<Float> = getValue(NOTIFICATION_PRIORITY_KEY, 3f)
    override suspend fun setNotificationPriority(priority: Float) =
        setValue(NOTIFICATION_PRIORITY_KEY, priority)

    override fun isBiometricAuthEnabled(): Flow<Boolean> = getValue(BIOMETRIC_AUTH_KEY, false)
    override suspend fun setBiometricAuthEnabled(enabled: Boolean) =
        setValue(BIOMETRIC_AUTH_KEY, enabled)

    override fun isAnalyticsEnabled(): Flow<Boolean> = getValue(ANALYTICS_KEY, true)
    override suspend fun setAnalyticsEnabled(enabled: Boolean) = setValue(ANALYTICS_KEY, enabled)

    override fun isDynamicColorEnabled(): Flow<Boolean> = getValue(DYNAMIC_COLOR_KEY, false)
    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        setValue(DYNAMIC_COLOR_KEY, enabled)
    }

    override fun isSystemThemeEnabled(): Flow<Boolean> = getValue(SYSTEM_THEME_KEY, true)
    override suspend fun setSystemThemeEnabled(enabled: Boolean) = setValue(SYSTEM_THEME_KEY, enabled)
}
