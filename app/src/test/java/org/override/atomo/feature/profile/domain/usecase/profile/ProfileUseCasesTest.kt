/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.domain.usecase.profile

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.profile.domain.model.Profile
import org.override.atomo.feature.profile.domain.repository.ProfileRepository

class ProfileUseCasesTest {

    private lateinit var repository: ProfileRepository
    private lateinit var getProfileUseCase: GetProfileUseCase
    private lateinit var syncProfileUseCase: SyncProfileUseCase
    private lateinit var updateProfileUseCase: UpdateProfileUseCase

    private val user123 = "user123"
    private val mockProfile = Profile(
        id = user123,
        username = "testuser",
        displayName = "Test User",
        avatarUrl = null,
        socialLinks = null,
        createdAt = 0L,
        updatedAt = 0L
    )

    @Before
    fun setUp() {
        repository = mockk()
        getProfileUseCase = GetProfileUseCase(repository)
        syncProfileUseCase = SyncProfileUseCase(repository)
        updateProfileUseCase = UpdateProfileUseCase(repository)
    }

    /**
     * Verifica que el flujo devuelto por el caso de uso coincida con el perfil esperado para el usuario.
     */
    @Test
    fun `GetProfileUseCase devuelve el flujo esperado de perfil`() = runTest {
        every { repository.getProfileFlow(user123) } returns flowOf(mockProfile)

        getProfileUseCase(user123).test {
            assertEquals(mockProfile, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso de uso de sincronización llame al repositorio
     * y devuelva un resultado exitoso conteniendo el perfil asíncrono consultado.
     */
    @Test
    fun `SyncProfileUseCase llama al repositorio devolviendo el perfil de forma exitosa`() = runTest {
        coEvery { repository.syncProfile(user123) } returns Result.success(mockProfile)

        val result = syncProfileUseCase(user123)
        assertTrue(result.isSuccess)
        assertEquals(mockProfile, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso de actualización llame al repositorio enviando
     * las modificaciones y devuelva un resultado exitoso con el perfil editado.
     */
    @Test
    fun `UpdateProfileUseCase llama al repositorio para actualizar y retorna resultado exitoso`() = runTest {
        coEvery { repository.updateProfile(mockProfile) } returns Result.success(mockProfile)

        val result = updateProfileUseCase(mockProfile)
        assertTrue(result.isSuccess)
        assertEquals(mockProfile, result.getOrNull())
    }
}
