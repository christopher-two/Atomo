/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.portfolio.presentation

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
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PortfolioViewModel
    private val portfolioUseCases: PortfolioUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()

    private val testPortfolio = Portfolio(
        id = "port123",
        userId = "user123",
        title = "My Portfolio",
        description = "Desc",
        isVisible = true,
        templateId = "minimalist",
        primaryColor = "#000000",
        fontFamily = "Inter",
        createdAt = 1000L
    )

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { portfolioUseCases.getPortfolios("user123") } returns flowOf(listOf(testPortfolio))
        coEvery { canCreateServiceUseCase("user123", ServiceType.PORTFOLIO) } returns CanCreateResult.Success
        
        viewModel = PortfolioViewModel(portfolioUseCases, canCreateServiceUseCase, sessionRepository)
    }

    @Test
    fun `initial load should fetch portfolios`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.portfolios.size)
            assertEquals(testPortfolio, state.portfolios.first())
        }
    }

    @Test
    fun `OpenPortfolio should update editingPortfolio`() = runTest {
        viewModel.state.test {
            awaitItem()
            
            viewModel.onAction(PortfolioAction.OpenPortfolio("port123"))
            
            val state = awaitItem()
            assertEquals(testPortfolio, state.editingPortfolio)
            assertFalse(state.isEditing)
        }
    }

    @Test
    fun `ToggleEditMode should toggle isEditing`() = runTest {
        viewModel.state.test {
            awaitItem()
            
            viewModel.onAction(PortfolioAction.ToggleEditMode)
            
            val state = awaitItem()
            assertTrue(state.isEditing)
        }
    }
}
