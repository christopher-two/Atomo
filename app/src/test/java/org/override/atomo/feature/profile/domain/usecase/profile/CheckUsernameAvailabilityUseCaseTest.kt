/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.domain.usecase.profile

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.profile.domain.repository.ProfileRepository

class CheckUsernameAvailabilityUseCaseTest {

    private lateinit var repository: ProfileRepository
    private lateinit var useCase: CheckUsernameAvailabilityUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CheckUsernameAvailabilityUseCase(repository)
    }

    /**
     * Verifica que el caso de uso devuelva [true] cuando el nombre de usuario está disponible 
     * tras validarlo en el repositorio.
     */
    @Test
    fun `invoke retorna true cuando el nombre de usuario se encuentra disponible`() = runTest {
        val username = "available_user"
        coEvery { repository.checkUsernameAvailability(username) } returns true

        val result = useCase(username)
        assertEquals(true, result)
    }

    /**
     * Verifica que el caso de uso devuelva [false] cuando el nombre de usuario requerido
     * ya ha sido tomado y está atado a otro registro.
     */
    @Test
    fun `invoke retorna false cuando el nombre de usuario ya ha sido tomado`() = runTest {
        val username = "taken_user"
        coEvery { repository.checkUsernameAvailability(username) } returns false

        val result = useCase(username)
        assertEquals(false, result)
    }
}
