/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.home.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.usecase.subscription.GetExistingServicesUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private val homeNavigation: HomeNavigation = mockk(relaxed = true)
    private val rootNavigation: RootNavigation = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk()
    private val subscriptionUseCases: SubscriptionUseCases = mockk()
    private val getExistingServices: GetExistingServicesUseCase = mockk()
    private val syncAllServices: SyncAllServicesUseCase = mockk()

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { subscriptionUseCases.syncPlans() } returns Result.success(emptyList())
        coEvery { subscriptionUseCases.syncSubscription("user123") } returns Result.success(mockk())
        coEvery { subscriptionUseCases.getSubscription("user123") } returns flowOf(null)
        coEvery { subscriptionUseCases.getPlans() } returns flowOf(emptyList())
        coEvery { getExistingServices("user123") } returns emptyMap()
        
        viewModel = HomeViewModel(
            homeNavigation, rootNavigation, sessionRepository,
            subscriptionUseCases, getExistingServices, syncAllServices
        )
    }

    @Test
    fun `initial state should load data`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.currentSubscription)
        }
    }
}
