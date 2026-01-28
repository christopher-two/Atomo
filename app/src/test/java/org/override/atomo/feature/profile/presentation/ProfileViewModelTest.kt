/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.usecase.profile.CheckUsernameAvailabilityUseCase
import org.override.atomo.domain.usecase.profile.GetProfileUseCase
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.profile.SyncProfileUseCase
import org.override.atomo.domain.usecase.profile.UpdateProfileUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProfileViewModel
    private val useCases: ProfileUseCases = mockk()
    private val sessionRepository: SessionRepository = mockk()
    
    private val getProfileUseCase: GetProfileUseCase = mockk()
    private val syncProfileUseCase: SyncProfileUseCase = mockk()
    private val updateProfileUseCase: UpdateProfileUseCase = mockk()
    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase = mockk()

    private val testProfile = Profile(
        id = "user123",
        username = "testuser",
        displayName = "Test User",
        avatarUrl = null,
        socialLinks = null,
        createdAt = 1000L,
        updatedAt = 1000L
    )

    @Before
    fun setUp() {
        coEvery { useCases.getProfile } returns getProfileUseCase
        coEvery { useCases.syncProfile } returns syncProfileUseCase
        coEvery { useCases.updateProfile } returns updateProfileUseCase
        coEvery { useCases.checkUsernameAvailability } returns checkUsernameAvailabilityUseCase
        
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { getProfileUseCase(any()) } returns flowOf(testProfile)
        
        viewModel = ProfileViewModel(useCases, sessionRepository)
    }

    @Test
    fun `initial state should load profile`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testProfile, state.profile)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `EnterEditMode should populate edit fields`() = runTest {
        viewModel.onAction(ProfileAction.EnterEditMode)
        
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEditing)
            assertEquals("testuser", state.editUsername)
            assertEquals("Test User", state.editDisplayName)
        }
    }

    @Test
    fun `UpdateDisplayName should update state`() = runTest {
        viewModel.onAction(ProfileAction.UpdateDisplayName("New Name"))
        
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("New Name", state.editDisplayName)
        }
    }

    @Test
    fun `CancelEdit should reset isEditing`() = runTest {
        viewModel.onAction(ProfileAction.EnterEditMode)
        viewModel.onAction(ProfileAction.CancelEdit)
        
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isEditing)
        }
    }
}
