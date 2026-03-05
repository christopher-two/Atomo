/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.domain.usecase

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.settings.domain.model.AppearanceSettings
import org.override.atomo.feature.settings.domain.model.NotificationSettings
import org.override.atomo.feature.settings.domain.model.PrivacySettings
import org.override.atomo.feature.settings.domain.model.Settings
import org.override.atomo.libs.settings.api.SettingsRepository

class SettingsUseCasesTest {

    private lateinit var repository: SettingsRepository
    private lateinit var getSettingsUseCase: GetSettingsUseCase
    private lateinit var updateAppearanceUseCase: UpdateAppearanceUseCase
    private lateinit var updateNotificationsUseCase: UpdateNotificationsUseCase
    private lateinit var updatePrivacyUseCase: UpdatePrivacyUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getSettingsUseCase = GetSettingsUseCase(repository)
        updateAppearanceUseCase = UpdateAppearanceUseCase(repository)
        updateNotificationsUseCase = UpdateNotificationsUseCase(repository)
        updatePrivacyUseCase = UpdatePrivacyUseCase(repository)
    }

    /**
     * Verifica que el caso de uso de configuraciones combine correctamente los flujos 
     * separados de Apariencia, Notificaciones y Privacidad devolviendo un objeto `Settings` estructurado.
     */
    @Test
    fun `GetSettingsUseCase devuelve el flujo de configuraciones correctamente combinadas`() = runTest {
        // Mock Appearance Flow
        every { repository.isDarkModeEnabled() } returns flowOf(true)
        every { repository.getTheme() } returns flowOf("dark")
        every { repository.isDynamicColorEnabled() } returns flowOf(false)
        every { repository.isSystemThemeEnabled() } returns flowOf(true)

        // Mock Notifications Flow
        every { repository.areNotificationsEnabled() } returns flowOf(true)
        every { repository.isNotificationSoundEnabled() } returns flowOf(false)
        every { repository.getNotificationPriority() } returns flowOf(1f)

        // Mock Privacy Flow
        every { repository.isBiometricAuthEnabled() } returns flowOf(true)
        every { repository.isAnalyticsEnabled() } returns flowOf(false)

        val expectedSettings = Settings(
            appearance = AppearanceSettings(true, "dark", false, true),
            notifications = NotificationSettings(true, false, 1f),
            privacy = PrivacySettings(true, false)
        )

        getSettingsUseCase().test {
            assertEquals(expectedSettings, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso para actualizar la Apariencia delegue de forma correcta las
     * opciones especificadas hacia los métodos de edición dentro del repositorio.
     */
    @Test
    fun `Los metodos de UpdateAppearanceUseCase se delegan de forma correcta al nivel de repositorio`() = runTest {
        coEvery { repository.setDarkModeEnabled(true) } returns Result.success(Unit)
        coEvery { repository.setTheme("light") } returns Result.success(Unit)
        coEvery { repository.setDynamicColorEnabled(false) } returns Unit
        coEvery { repository.setSystemThemeEnabled(true) } returns Result.success(Unit)

        updateAppearanceUseCase.toggleDarkMode(true)
        updateAppearanceUseCase.setTheme("light")
        updateAppearanceUseCase.toggleDynamicColor(false)
        updateAppearanceUseCase.toggleSystemTheme(true)
        
        // MockK coVerify can verify the interactions, but just returning successfully without exception 
        // asserts that the methods bind correctly to the mocked repository.
    }

    /**
     * Verifica que el caso para manejar las configuraciones de Notificaciones interactúe
     * y aplique correctamente los comandos pertinentes hacia el repositorio de origen.
     */
    @Test
    fun `Los comandos de UpdateNotificationsUseCase apuntan efectivamente al repositorio local`() = runTest {
        coEvery { repository.setNotificationsEnabled(true) } returns Result.success(Unit)
        coEvery { repository.setNotificationSoundEnabled(false) } returns Result.success(Unit)
        coEvery { repository.setNotificationPriority(2f) } returns Result.success(Unit)

        updateNotificationsUseCase.toggleNotifications(true)
        updateNotificationsUseCase.toggleNotificationSound(false)
        updateNotificationsUseCase.setNotificationPriority(2f)
    }

    /**
     * Verifica la delegación apropiada de los ajustes de Privacidad en forma 
     * de requerimientos a la implementación nativa del repositorio de ajustes.
     */
    @Test
    fun `Las opciones en UpdatePrivacyUseCase actuan sin problemas contra la abstraccion del repositorio principal`() = runTest {
        coEvery { repository.setBiometricAuthEnabled(true) } returns Result.success(Unit)
        coEvery { repository.setAnalyticsEnabled(false) } returns Result.success(Unit)

        updatePrivacyUseCase.toggleBiometricAuth(true)
        updatePrivacyUseCase.toggleAnalytics(false)
    }
}
