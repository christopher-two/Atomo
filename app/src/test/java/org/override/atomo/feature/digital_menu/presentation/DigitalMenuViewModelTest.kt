/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

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
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.model.MenuCategory
import org.override.atomo.feature.digital_menu.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanCreateResult
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class DigitalMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DigitalMenuViewModel
    private val menuUseCases: MenuUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()

    private val snackbarManager: SnackbarManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

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
        
        viewModel = DigitalMenuViewModel(
            sessionRepository,
            menuUseCases,
            canCreateServiceUseCase,
            snackbarManager
        )
    }

    @Test
    fun `initial load should fetch menus and auto-select first one`() = runTest {
        // The viewModel init already triggers loadMenus
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.menus.size)
            assertEquals(testMenu.id, state.editingMenu?.id)
        }
    }

    @Test
    fun `save new dish should call upsertDish`() = runTest {
        coEvery { menuUseCases.upsertDish(any(), any(), any(), any(), any(), any(), any(), any()) }
        
        viewModel.state.test {
            awaitItem() // Initial load
            
            // Edit menu must be set
            viewModel.onAction(DigitalMenuAction.OpenMenu(testMenu.id))
            awaitItem()
            
            // Act
            viewModel.onAction(DigitalMenuAction.SaveDish("Pizza", "Good", 10.0, null, null))
            
            // Skip loading states
            awaitItem()
            awaitItem()
            
            // Assert
            coVerify { menuUseCases.upsertDish("user123", testMenu.id, "Pizza", "Good", 10.0, null, null, null) }
        }
    }

    @Test
    fun `save category should call createCategory`() = runTest {
        coEvery { menuUseCases.createCategory(any()) }
        
        viewModel.state.test {
            awaitItem() // Initial load
            
            // Edit menu must be set
            viewModel.onAction(DigitalMenuAction.OpenMenu(testMenu.id))
            awaitItem()
            
            viewModel.onAction(DigitalMenuAction.SaveCategory("Drinks"))
            
            awaitItem()
            awaitItem()
            
            coVerify { menuUseCases.createCategory(match { it.name == "Drinks" && it.menuId == testMenu.id }) }
        }
    }

    @Test
    fun `delete category should remove it from state and update dishes`() = runTest {
        // Arrange
        val categoryId = "cat123"
        val category = MenuCategory(categoryId, testMenu.id, "Food", 0, 1000L)
        val dish = Dish("dish1", testMenu.id, categoryId, "Pasta", "Desc", 10.0, null, true, 0, 1000L)
        
        val menuWithData = testMenu.copy(categories = listOf(category), dishes = listOf(dish))
        coEvery { menuUseCases.getMenus("user123") } returns flowOf(listOf(menuWithData))
        coEvery { menuUseCases.deleteCategory(categoryId) } returns Result.success(Unit)
        
        // Re-init to load new mock data
        viewModel = DigitalMenuViewModel(
            sessionRepository, menuUseCases, canCreateServiceUseCase, snackbarManager
        )

        viewModel.state.test {
            awaitItem() // Load state
            
            viewModel.onAction(DigitalMenuAction.DeleteCategory(category))
            
            val state = awaitItem()
            assert(state.editingMenu?.categories?.isEmpty() == true)
            assertEquals(null, state.editingMenu?.dishes?.first()?.categoryId)
        }
    }

    @Test
    fun `save menu failure should update state with error message`() = runTest {
        coEvery { menuUseCases.updateMenu(any()) } returns Result.failure(Exception("Network Error"))

        viewModel.state.test {
            // Consume initial load items
            while (awaitItem().isLoading) { /* wait */ }
            
            viewModel.onAction(DigitalMenuAction.ToggleEditMode)
            awaitItem() // isEditing = true
            
            viewModel.onAction(DigitalMenuAction.SaveMenu)
            
            // Should get: isLoading = true, then isLoading = false with error
            var state = awaitItem()
            while (!state.isLoading && state.error == null) {
                state = awaitItem()
            }
            if (state.isLoading) {
                state = awaitItem() // Get the one with error
            }
            
            assertEquals("Network Error", state.error)
        }
    }


}
