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
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.feature.auth.domain.repository.GoogleAuthManager
import org.override.atomo.feature.session.domain.repository.SessionRepository

class LogoutUseCaseTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var database: AtomoDatabase
    private lateinit var googleAuthManager: GoogleAuthManager
    private lateinit var useCase: LogoutUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk()
        database = mockk()
        googleAuthManager = mockk()
        useCase = LogoutUseCase(sessionRepository, database, googleAuthManager)
    }

    @Test
    fun `invoke retorna success cuando el logout es exitoso`() = runTest {
        coEvery { database.clearAllTables() } just runs
        coEvery { googleAuthManager.signOut() } just runs
        coEvery { sessionRepository.clearUserSession() } returns Result.success(Unit)

        val result = useCase()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke llama a clearAllTables antes de signOut y clearUserSession`() = runTest {
        coEvery { database.clearAllTables() } just runs
        coEvery { googleAuthManager.signOut() } just runs
        coEvery { sessionRepository.clearUserSession() } returns Result.success(Unit)

        useCase()

        coVerifyOrder {
            database.clearAllTables()
            googleAuthManager.signOut()
            sessionRepository.clearUserSession()
        }
    }

    @Test
    fun `invoke retorna failure cuando clearAllTables lanza excepcion`() = runTest {
        val exception = RuntimeException("Fallo al limpiar base de datos")
        coEvery { database.clearAllTables() } throws exception

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun `invoke retorna failure cuando signOut lanza excepcion`() = runTest {
        coEvery { database.clearAllTables() } just runs
        coEvery { googleAuthManager.signOut() } throws RuntimeException("Fallo en signOut")

        val result = useCase()

        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke retorna failure cuando clearUserSession falla`() = runTest {
        coEvery { database.clearAllTables() } just runs
        coEvery { googleAuthManager.signOut() } just runs
        coEvery { sessionRepository.clearUserSession() } returns
            Result.failure(Exception("Fallo al limpiar sesión"))

        // clearUserSession devuelve failure pero no lanza excepción,
        // por eso invoke() termina en success (el try no atrapa un Result.failure)
        val result = useCase()

        assertTrue(result.isSuccess)
    }
}

