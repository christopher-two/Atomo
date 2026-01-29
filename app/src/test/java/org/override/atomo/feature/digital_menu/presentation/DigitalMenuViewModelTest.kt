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
import io.mockk.coVerify
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

import android.content.Context
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.subscription.CanAddItemResult
import org.override.atomo.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.domain.usecase.subscription.GetServiceLimitsUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.libs.image.api.ImageManager

class DigitalMenuViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DigitalMenuViewModel
    private val menuUseCases: MenuUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()
    private val canAddDishUseCase: CanAddDishUseCase = mockk()
    private val uploadDishImageUseCase: UploadDishImageUseCase = mockk()
    private val deleteDishImageUseCase: DeleteDishImageUseCase = mockk()
    private val getServiceLimitsUseCase: GetServiceLimitsUseCase = mockk()
    private val subscriptionUseCases: SubscriptionUseCases = mockk()
    private val imageManager: ImageManager = mockk()
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
            getServiceLimitsUseCase,
            subscriptionUseCases,
            canCreateServiceUseCase,
            canAddDishUseCase,
            uploadDishImageUseCase,
            deleteDishImageUseCase,
            imageManager
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
    fun `save new dish should check limits and update state`() = runTest {
        // Arrange
        coEvery { canAddDishUseCase("user123", testMenu.id) } returns CanAddItemResult.Success
        
        viewModel.state.test {
            awaitItem() // Initial load
            
            // Act
            viewModel.onAction(DigitalMenuAction.SaveDish("Pizza", "Good", 10.0, null, null))
            
            // Skip loading state and get updated state
            val state = awaitItem()
            
            // Assert
            val addedDish = state.editingMenu?.dishes?.find { it.name == "Pizza" }
            assert(addedDish != null)
            assertEquals("Good", addedDish?.description)
            assertEquals(10.0, addedDish?.price ?: 0.0, 0.0)
        }
    }
}
