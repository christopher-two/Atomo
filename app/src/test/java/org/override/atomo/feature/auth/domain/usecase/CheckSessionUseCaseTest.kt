/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.domain.usecase

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.session.domain.repository.SessionRepository

class CheckSessionUseCaseTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var useCase: CheckSessionUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk()
        useCase = CheckSessionUseCase(sessionRepository)
    }

    @Test
    fun `invoke emite true cuando hay sesion activa`() = runTest {
        every { sessionRepository.isUserLoggedIn() } returns flowOf(true)

        useCase().test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }

        // verify() de mockk invoca el método para contar las llamadas; el Flow resultante no se consume intencionalmente.
        @Suppress("UNUSED_EXPRESSION")
        verify(exactly = 1) { sessionRepository.isUserLoggedIn() }
    }

    @Test
    fun `invoke emite false cuando no hay sesion activa`() = runTest {
        every { sessionRepository.isUserLoggedIn() } returns flowOf(false)

        useCase().test {
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emite multiples valores reflejando cambios de sesion`() = runTest {
        every { sessionRepository.isUserLoggedIn() } returns flowOf(false, true, false)

        useCase().test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }
}


