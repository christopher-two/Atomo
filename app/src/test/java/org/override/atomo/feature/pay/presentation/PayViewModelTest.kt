/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.pay.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class PayViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PayViewModel
    private val subscriptionUseCases: SubscriptionUseCases = mockk()
    private val sessionRepository: SessionRepository = mockk()
    private val rootNavigation: RootNavigation = mockk()

    @Before
    fun setUp() {
        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { subscriptionUseCases.syncPlans() } returns Result.success(emptyList())
        coEvery { subscriptionUseCases.syncSubscription("user123") } returns Result.success(mockk())
        coEvery { subscriptionUseCases.getPlans() } returns flowOf(emptyList())
        coEvery { subscriptionUseCases.getSubscription("user123") } returns flowOf(null)
        
        viewModel = PayViewModel(subscriptionUseCases, sessionRepository, rootNavigation)
    }

    @Test
    fun `initial state should have plans and subscription`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptyList<Any>(), state.plans)
            assertEquals(null, state.currentSubscription)
        }
    }
}
