/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class DigitalMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DigitalMenuViewModel
    private val menuUseCases: MenuUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()

    private val testMenu = Menu(
        id = "menu123",
        userId = "user123",
        name = "My Menu",
        description = "Desc",
        isActive = true,
        templateId = "minimalist",
        primaryColor = "#000000",
        fontFamily = "Inter",
        logoUrl = null,
        createdAt = 1000L,
        dishes = emptyList()
    )

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { menuUseCases.getMenus("user123") } returns flowOf(listOf(testMenu))
        coEvery { canCreateServiceUseCase("user123", ServiceType.DIGITAL_MENU) } returns CanCreateResult.Success
        
        viewModel = DigitalMenuViewModel(menuUseCases, canCreateServiceUseCase, sessionRepository)
    }

    @Test
    fun `initial load should fetch menus`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.menus.size)
            assertEquals(testMenu, state.menus.first())
        }
    }
}
