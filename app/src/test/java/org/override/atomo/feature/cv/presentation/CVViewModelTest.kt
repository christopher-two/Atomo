/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.cv.presentation

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
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class CVViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CVViewModel
    private val cvUseCases: CvUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()
    private val snackbarManager: SnackbarManager = mockk(relaxed = true)

    private val testCv = Cv(
        id = "cv123",
        userId = "user123",
        title = "My CV",
        professionalSummary = "Summary",
        isVisible = true,
        templateId = "standard",
        primaryColor = "#000000",
        fontFamily = "Inter",
        createdAt = 1000L
    )

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { cvUseCases.getCvs("user123") } returns flowOf(listOf(testCv))
        coEvery { canCreateServiceUseCase("user123", ServiceType.CV) } returns CanCreateResult.Success

        viewModel =
            CVViewModel(cvUseCases, canCreateServiceUseCase, sessionRepository, snackbarManager)
    }

    @Test
    fun `initial load should fetch cvs`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.cvs.size)
            assertEquals(testCv, state.cvs.first())
        }
    }

    @Test
    fun `OpenCv should set editingCv`() = runTest {
        viewModel.state.test {
            awaitItem()
            
            viewModel.onAction(CVAction.OpenCv("cv123"))
            
            val state = awaitItem()
            assertEquals(testCv, state.editingCv)
        }
    }
}
