/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.invitation.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class InvitationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: InvitationViewModel
    private val invitationUseCases: InvitationUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()
    private val snackbarManager: SnackbarManager = mockk(relaxed = true)

    private val testInvitation = Invitation(
        id = "inv123",
        userId = "user123",
        eventName = "My Event",
        eventDate = 2000L,
        location = "Venue",
        description = "Desc",
        isActive = true,
        templateId = "classic",
        primaryColor = "#000000",
        fontFamily = "Inter",
        createdAt = 1000L,
        responses = emptyList()
    )

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { invitationUseCases.getInvitations("user123") } returns flowOf(listOf(testInvitation))
        coEvery { canCreateServiceUseCase("user123", ServiceType.INVITATION) } returns CanCreateResult.Success

        viewModel = InvitationViewModel(
            invitationUseCases,
            canCreateServiceUseCase,
            sessionRepository,
            snackbarManager
        )
    }

    @Test
    fun `initial load should fetch invitations`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.invitations.size)
            assertEquals(testInvitation, state.invitations.first())
        }
    }
}
