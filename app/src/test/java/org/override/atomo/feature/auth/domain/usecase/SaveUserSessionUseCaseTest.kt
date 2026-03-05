/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.libs.session.api.SessionRepository

class SaveUserSessionUseCaseTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var useCase: SaveUserSessionUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk()
        useCase = SaveUserSessionUseCase(sessionRepository)
    }

    @Test
    fun `invoke retorna success cuando la sesion se guarda correctamente`() = runTest {
        val userId = "user-abc-123"
        coEvery { sessionRepository.saveUserSession(userId) } returns Result.success(Unit)

        val result = useCase(userId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { sessionRepository.saveUserSession(userId) }
    }

    @Test
    fun `invoke retorna failure cuando el repositorio falla`() = runTest {
        val userId = "user-abc-123"
        val exception = Exception("Error al escribir en DataStore")
        coEvery { sessionRepository.saveUserSession(userId) } returns Result.failure(exception)

        val result = useCase(userId)

        assertTrue(result.isFailure)
        assertEquals("Error al escribir en DataStore", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke llama al repositorio con el userId correcto`() = runTest {
        val userId = "uid-xyz-789"
        coEvery { sessionRepository.saveUserSession(any()) } returns Result.success(Unit)

        useCase(userId)

        coVerify { sessionRepository.saveUserSession(userId) }
    }

    @Test
    fun `invoke con userId vacio sigue delegando al repositorio`() = runTest {
        val emptyUserId = ""
        coEvery { sessionRepository.saveUserSession(emptyUserId) } returns Result.success(Unit)

        val result = useCase(emptyUserId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { sessionRepository.saveUserSession(emptyUserId) }
    }
}

