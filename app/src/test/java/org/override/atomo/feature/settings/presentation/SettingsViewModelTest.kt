/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.feature.auth.domain.usecase.LogoutUseCase
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.settings.domain.model.AppearanceSettings
import org.override.atomo.feature.settings.domain.model.NotificationSettings
import org.override.atomo.feature.settings.domain.model.PrivacySettings
import org.override.atomo.feature.settings.domain.model.Settings
import org.override.atomo.feature.settings.domain.usecase.SettingsUseCases
import org.override.atomo.util.MainDispatcherRule

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsViewModel
    private val useCases: SettingsUseCases = mockk()
    private val rootNavigation: RootNavigation = mockk(relaxed = true)
    private val logoutUseCase: LogoutUseCase = mockk()
    private val homeNavigation: HomeNavigation = mockk(relaxed = true)

    private val testSettings = Settings(
        appearance = AppearanceSettings(false, "auto", false, false),
        notifications = NotificationSettings(true, true, 0f),
        privacy = PrivacySettings(false, true)
    )

    @Before
    fun setUp() {
        coEvery { useCases.getSettings() } returns flowOf(testSettings)
        
        viewModel = SettingsViewModel(
            useCases, rootNavigation, logoutUseCase, homeNavigation
        )
    }

    @Test
    fun `initial state should load settings`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
        }
    }
}
