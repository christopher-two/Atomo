/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.domain.usecase

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.auth.domain.model.ExternalAuthResult
import org.override.atomo.feature.auth.domain.repository.GoogleAuthManager

class ContinueWithGoogleUseCaseTest {

    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var context: Context
    private lateinit var useCase: ContinueWithGoogleUseCase

    @Before
    fun setUp() {
        googleAuthManager = mockk()
        context = mockk(relaxed = true)
        useCase = ContinueWithGoogleUseCase(googleAuthManager)
    }

    @Test
    fun `invoke retorna Success cuando el login es exitoso`() = runTest {
        val expectedUserId = "user-123"
        coEvery { googleAuthManager.signIn(context) } returns
            Result.success(ExternalAuthResult.Success(expectedUserId))

        val result = useCase(context)

        assertTrue(result.isSuccess)
        val authResult = result.getOrThrow()
        assertTrue(authResult is ExternalAuthResult.Success)
        assertEquals(expectedUserId, (authResult as ExternalAuthResult.Success).userId)
        coVerify(exactly = 1) { googleAuthManager.signIn(context) }
    }

    @Test
    fun `invoke retorna Cancelled cuando el usuario cancela`() = runTest {
        coEvery { googleAuthManager.signIn(context) } returns
            Result.success(ExternalAuthResult.Cancelled)

        val result = useCase(context)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow() is ExternalAuthResult.Cancelled)
    }

    @Test
    fun `invoke retorna failure cuando ocurre un error de red`() = runTest {
        val exception = Exception("Error de conexión")
        coEvery { googleAuthManager.signIn(context) } returns Result.failure(exception)

        val result = useCase(context)

        assertTrue(result.isFailure)
        assertEquals("Error de conexión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke retorna Error cuando las credenciales son invalidas`() = runTest {
        coEvery { googleAuthManager.signIn(context) } returns
            Result.success(ExternalAuthResult.Error("Credenciales inválidas"))

        val result = useCase(context)

        assertTrue(result.isSuccess)
        val authResult = result.getOrThrow()
        assertTrue(authResult is ExternalAuthResult.Error)
        assertEquals("Credenciales inválidas", (authResult as ExternalAuthResult.Error).message)
    }
}

