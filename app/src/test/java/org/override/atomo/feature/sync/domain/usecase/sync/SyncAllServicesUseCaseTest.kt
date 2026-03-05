/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.sync.domain.usecase.sync

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.cv.domain.usecase.cv.CvUseCases
import org.override.atomo.feature.digital_menu.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.invitation.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.feature.portfolio.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.feature.profile.domain.usecase.profile.ProfileUseCases
import org.override.atomo.feature.shop.domain.usecase.shop.ShopUseCases

class SyncAllServicesUseCaseTest {

    private lateinit var profileUseCases: ProfileUseCases
    private lateinit var menuUseCases: MenuUseCases
    private lateinit var portfolioUseCases: PortfolioUseCases
    private lateinit var cvUseCases: CvUseCases
    private lateinit var shopUseCases: ShopUseCases
    private lateinit var invitationUseCases: InvitationUseCases

    private lateinit var useCase: SyncAllServicesUseCase

    @Before
    fun setUp() {
        profileUseCases = mockk()
        menuUseCases = mockk()
        portfolioUseCases = mockk()
        cvUseCases = mockk()
        shopUseCases = mockk()
        invitationUseCases = mockk()

        useCase = SyncAllServicesUseCase(
            profileUseCases = profileUseCases,
            menuUseCases = menuUseCases,
            portfolioUseCases = portfolioUseCases,
            cvUseCases = cvUseCases,
            shopUseCases = shopUseCases,
            invitationUseCases = invitationUseCases
        )
    }

    /**
     * Verifica que el caso de uso principal de sincronización retorne éxito
     * si todas las partes subyacentes se completan sin arrojar excepciones.
     */
    @Test
    fun `invoke finaliza exitosamente cuando todos los servicios sincronizan bien`() = runTest {
        val userId = "user123"
        coEvery { profileUseCases.syncProfile(userId) } returns Result.success(mockk())
        coEvery { menuUseCases.syncMenus(userId) } returns Result.success(emptyList())
        coEvery { portfolioUseCases.syncPortfolios(userId) } returns Result.success(emptyList())
        coEvery { cvUseCases.syncCvs(userId) } returns Result.success(emptyList())
        coEvery { shopUseCases.syncShops(userId) } returns Result.success(emptyList())
        coEvery { invitationUseCases.syncInvitations(userId) } returns Result.success(emptyList())

        val result = useCase(userId)
        assertTrue(result.isSuccess)
    }

    /**
     * Verifica el fallo prematuro por fallo de red o parseo en el caso de la
     * sincronización del perfil del usuario la cual es obligatoria para el entorno.
     */
    @Test
    fun `invoke falla tempranamente cuando no logra sincronizar el perfil base`() = runTest {
        val userId = "user123"
        val exception = Exception("Profile sync failed")
        
        coEvery { profileUseCases.syncProfile(userId) } returns Result.failure(exception)
        // Services shouldn't be awaited or might fail, but let's mock them as successes
        coEvery { menuUseCases.syncMenus(userId) } returns Result.success(emptyList())
        coEvery { portfolioUseCases.syncPortfolios(userId) } returns Result.success(emptyList())
        coEvery { cvUseCases.syncCvs(userId) } returns Result.success(emptyList())
        coEvery { shopUseCases.syncShops(userId) } returns Result.success(emptyList())
        coEvery { invitationUseCases.syncInvitations(userId) } returns Result.success(emptyList())

        val result = useCase(userId)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        
        // Even if some coroutines started, the overall result should match the profile failure.
    }

    /**
     * Simula la falla de alguno de los múltiples elementos (por ejemplo los Menús) a sincronizar, 
     * validando que la propagación de una sola excepción se marque como un fallo de la operación total.
     */
    @Test
    fun `invoke marca fallo si al menos uno de los servicios falla`() = runTest {
        val userId = "user123"
        val exception = Exception("Menu sync failed")

        coEvery { profileUseCases.syncProfile(userId) } returns Result.success(mockk())
        
        coEvery { menuUseCases.syncMenus(userId) } returns Result.failure(exception)
        coEvery { portfolioUseCases.syncPortfolios(userId) } returns Result.success(emptyList())
        coEvery { cvUseCases.syncCvs(userId) } returns Result.success(emptyList())
        coEvery { shopUseCases.syncShops(userId) } returns Result.success(emptyList())
        coEvery { invitationUseCases.syncInvitations(userId) } returns Result.success(emptyList())

        val result = useCase(userId)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
