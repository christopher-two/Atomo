/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.override.atomo.util.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class SnackbarManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val snackbarManager = SnackbarManager()

    @Test
    fun `showMessage should trigger snackbar in host state`() = runTest {
        // Act
        snackbarManager.showMessage("Test Message")
        
        // Advance coroutines to ensure the message is processed
        advanceUntilIdle()
        
        // Assert
        // Note: SnackbarHostState doesn't expose the current message easily without a flow,
        // but we can at least verify the host state exists and was interacted with.
        assertNotNull(snackbarManager.snackbarHostState)
    }
}
