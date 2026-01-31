/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.presentation.viewmodel

import android.content.Context
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.core.common.RouteApp
import org.override.atomo.data.manager.SyncManager
import org.override.atomo.domain.usecase.onboarding.ShouldShowOnboardingUseCase
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.auth.domain.usecase.SaveUserSessionUseCase
import org.override.atomo.feature.auth.presentation.AuthAction
import org.override.atomo.feature.auth.presentation.AuthViewModel
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.auth.api.ExternalAuthResult
import org.override.atomo.util.MainDispatcherRule

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel
    private val continueWithGoogleUseCase: ContinueWithGoogleUseCase = mockk()
    private val saveUserSessionUseCase: SaveUserSessionUseCase = mockk()
    private val shouldShowOnboardingUseCase: ShouldShowOnboardingUseCase = mockk()
    private val rootNavigation: RootNavigation = mockk(relaxed = true)
    private val syncManager: SyncManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = AuthViewModel(
            continueWithGoogleUseCase,
            saveUserSessionUseCase,
            shouldShowOnboardingUseCase,
            rootNavigation,
            syncManager
        )
    }

    @Test
    fun `ContinueWithGoogle success should navigate to Home`() = runTest {
        val userId = "user123"
        coEvery { continueWithGoogleUseCase(any()) } returns Result.success(ExternalAuthResult.Success(userId))
        coEvery { saveUserSessionUseCase(userId) } returns Result.success(Unit)
        coEvery { shouldShowOnboardingUseCase(userId) } returns flowOf(false)

        viewModel.onAction(AuthAction.ContinueWithGoogle(context))

        viewModel.state.test {
            // Initial state
            var currentState = awaitItem()
            
            // Wait for loading to finish
            while (currentState.isLoading) {
                currentState = awaitItem()
            }
            
            coVerify { saveUserSessionUseCase(userId) }
            coVerify { rootNavigation.replaceWith(RouteApp.Home) }
        }
    }

    @Test
    fun `ContinueWithGoogle error should update state with error`() = runTest {
        val errorMessage = "Auth Failed"
        coEvery { continueWithGoogleUseCase(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.onAction(AuthAction.ContinueWithGoogle(context))

        viewModel.state.test {
            var currentState = awaitItem()
            
            // Wait for loading to start and then finish
            while (currentState.isLoading) {
                currentState = awaitItem()
            }
            // Skip initial state if needed or handle it
            if (currentState.error == null) {
                currentState = awaitItem()
            }
            
            assertEquals(errorMessage, currentState.error)
        }
    }
}
