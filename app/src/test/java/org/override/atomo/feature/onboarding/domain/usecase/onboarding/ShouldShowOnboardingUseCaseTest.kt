/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.domain.usecase.onboarding

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.cv.domain.repository.CvRepository
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.invitation.domain.repository.InvitationRepository
import org.override.atomo.feature.portfolio.domain.repository.PortfolioRepository
import org.override.atomo.feature.profile.domain.model.Profile
import org.override.atomo.feature.profile.domain.repository.ProfileRepository
import org.override.atomo.feature.shop.domain.model.Shop
import org.override.atomo.feature.shop.domain.repository.ShopRepository

class ShouldShowOnboardingUseCaseTest {

    private lateinit var profileRepository: ProfileRepository
    private lateinit var shopRepository: ShopRepository
    private lateinit var menuRepository: MenuRepository
    private lateinit var portfolioRepository: PortfolioRepository
    private lateinit var cvRepository: CvRepository
    private lateinit var invitationRepository: InvitationRepository

    private lateinit var useCase: ShouldShowOnboardingUseCase

    @Before
    fun setUp() {
        profileRepository = mockk()
        shopRepository = mockk()
        menuRepository = mockk()
        portfolioRepository = mockk()
        cvRepository = mockk()
        invitationRepository = mockk()

        useCase = ShouldShowOnboardingUseCase(
            profileRepository = profileRepository,
            shopRepository = shopRepository,
            menuRepository = menuRepository,
            portfolioRepository = portfolioRepository,
            cvRepository = cvRepository,
            invitationRepository = invitationRepository
        )

        // Default successful syncs
        coEvery { profileRepository.syncProfile(any()) } returns Result.success(mockk())
        coEvery { shopRepository.syncShops(any()) } returns Result.success(emptyList())
        coEvery { menuRepository.syncMenus(any()) } returns Result.success(emptyList())
        coEvery { portfolioRepository.syncPortfolios(any()) } returns Result.success(emptyList())
        coEvery { cvRepository.syncCvs(any()) } returns Result.success(emptyList())
        coEvery { invitationRepository.syncInvitations(any()) } returns Result.success(emptyList())
    }

    /**
     * Verifica que el caso de uso emita [true] cuando el perfil del usuario está incompleto.
     * En este caso, el displayName está vacío.
     */
    @Test
    fun `invoke emite true cuando el perfil esta incompleto`() = runTest {
        val incompleteProfile = Profile(
            id = "user123",
            username = "testuser",
            displayName = "", // Incomplete
            avatarUrl = null,
            socialLinks = null,
            createdAt = 0L,
            updatedAt = 0L
        )

        every { profileRepository.getProfileFlow("user123") } returns flowOf(incompleteProfile)
        
        // Mocking services (not empty, but profile is incomplete)
        val mockShop = mockk<Shop>()
        every { shopRepository.getShopsFlow("user123") } returns flowOf(listOf(mockShop))
        every { menuRepository.getMenusFlow("user123") } returns flowOf(emptyList())
        every { portfolioRepository.getPortfoliosFlow("user123") } returns flowOf(emptyList())
        every { cvRepository.getCvsFlow("user123") } returns flowOf(emptyList())
        every { invitationRepository.getInvitationsFlow("user123") } returns flowOf(emptyList())

        useCase("user123").test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso de uso emita [true] cuando el usuario tiene un perfil completo,
     * pero no tiene ningún servicio activo (tiendas, menús, portafolios, CVs o invitaciones).
     */
    @Test
    fun `invoke emite true cuando el usuario no tiene servicios activos`() = runTest {
        val completeProfile = Profile(
            id = "user123",
            username = "testuser",
            displayName = "Test User", // Complete
            avatarUrl = null,
            socialLinks = null,
            createdAt = 0L,
            updatedAt = 0L
        )

        every { profileRepository.getProfileFlow("user123") } returns flowOf(completeProfile)
        
        // Mocking empty services
        every { shopRepository.getShopsFlow("user123") } returns flowOf(emptyList())
        every { menuRepository.getMenusFlow("user123") } returns flowOf(emptyList())
        every { portfolioRepository.getPortfoliosFlow("user123") } returns flowOf(emptyList())
        every { cvRepository.getCvsFlow("user123") } returns flowOf(emptyList())
        every { invitationRepository.getInvitationsFlow("user123") } returns flowOf(emptyList())

        useCase("user123").test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso de uso emita [false] cuando el perfil está completo
     * y el usuario tiene al menos un servicio activo (por ejemplo, un menú).
     */
    @Test
    fun `invoke emite false cuando el perfil esta completo y tiene servicios activos`() = runTest {
        val completeProfile = Profile(
            id = "user123",
            username = "testuser",
            displayName = "Test User", // Complete
            avatarUrl = null,
            socialLinks = null,
            createdAt = 0L,
            updatedAt = 0L
        )

        every { profileRepository.getProfileFlow("user123") } returns flowOf(completeProfile)
        
        // Mocking at least one service
        val mockMenu = mockk<Menu>()
        every { shopRepository.getShopsFlow("user123") } returns flowOf(emptyList())
        every { menuRepository.getMenusFlow("user123") } returns flowOf(listOf(mockMenu))
        every { portfolioRepository.getPortfoliosFlow("user123") } returns flowOf(emptyList())
        every { cvRepository.getCvsFlow("user123") } returns flowOf(emptyList())
        every { invitationRepository.getInvitationsFlow("user123") } returns flowOf(emptyList())

        useCase("user123").test {
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }
}
