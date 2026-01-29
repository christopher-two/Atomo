/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.shop.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class ShopViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ShopViewModel
    private val shopUseCases: ShopUseCases = mockk()
    private val canCreateServiceUseCase: CanCreateServiceUseCase = mockk()
    private val sessionRepository: SessionRepository = mockk()

    private val testShop = Shop(
        id = "shop123",
        userId = "user123",
        name = "My Shop",
        description = "Desc",
        isActive = true,
        primaryColor = "#000000",
        fontFamily = "Inter",
        createdAt = 1000L,
        products = emptyList(),
        categories = emptyList()
    )

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { shopUseCases.getShops("user123") } returns flowOf(listOf(testShop))
        coEvery { canCreateServiceUseCase("user123", ServiceType.SHOP) } returns CanCreateResult.Success
        
        viewModel = ShopViewModel(shopUseCases, canCreateServiceUseCase, sessionRepository)
    }

    @Test
    fun `initial load should fetch shops`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.shops.size)
            assertEquals(testShop, state.shops.first())
        }
    }
}
