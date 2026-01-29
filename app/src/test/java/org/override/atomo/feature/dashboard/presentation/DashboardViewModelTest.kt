/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import android.util.Log
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.util.MainDispatcherRule

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel
    private val sessionRepository: SessionRepository = mockk()
    private val profileUseCases: ProfileUseCases = mockk()
    private val menuUseCases: MenuUseCases = mockk()
    private val portfolioUseCases: PortfolioUseCases = mockk()
    private val cvUseCases: CvUseCases = mockk()
    private val shopUseCases: ShopUseCases = mockk()
    private val invitationUseCases: InvitationUseCases = mockk()
    private val syncAllServices: SyncAllServicesUseCase = mockk()
    private val rootNavigation: RootNavigation = mockk()
    private val homeNavigation: HomeNavigation = mockk()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        coEvery { sessionRepository.getCurrentUserId() } returns flowOf("user123")
        coEvery { profileUseCases.getProfile("user123") } returns flowOf(null)
        coEvery { menuUseCases.getMenus("user123") } returns flowOf(emptyList())
        coEvery { portfolioUseCases.getPortfolios("user123") } returns flowOf(emptyList())
        coEvery { cvUseCases.getCvs("user123") } returns flowOf(emptyList())
        coEvery { shopUseCases.getShops("user123") } returns flowOf(emptyList())
        coEvery { invitationUseCases.getInvitations("user123") } returns flowOf(emptyList())
        coEvery { syncAllServices(any()) } returns Result.success(Unit)
        
        viewModel = DashboardViewModel(
            sessionRepository, profileUseCases, menuUseCases, portfolioUseCases,
            cvUseCases, shopUseCases, invitationUseCases, syncAllServices,
            rootNavigation, homeNavigation
        )
    }

    @Test
    fun `initial state should load services`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            // Even with empty lists, service modules are added to state
            assertEquals(5, state.services.size)
        }
    }
}
