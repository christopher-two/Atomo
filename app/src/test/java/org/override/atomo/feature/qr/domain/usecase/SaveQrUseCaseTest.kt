/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.domain.usecase

import android.graphics.Bitmap
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.qr.domain.repository.QrRepository

class SaveQrUseCaseTest {

    private lateinit var repository: QrRepository
    private lateinit var useCase: SaveQrUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SaveQrUseCase(repository)
    }

    /**
     * Verifica que el caso de uso al guardar un QR interactúe con el repositorio
     * y devuelva un resultado exitoso de forma correcta.
     */
    @Test
    fun `invoke llama al repositorio y devuelve un resultado exitoso`() = runTest {
        val mockBitmap = mockk<Bitmap>()
        val text = "test_text"
        
        coEvery { repository.saveQrCode(mockBitmap, text) } returns Result.success(Unit)

        val result = useCase(mockBitmap, text)
        assertTrue(result.isSuccess)
    }

    /**
     * Verifica que el caso de uso al intentar guardar un código QR interactuando con el repositorio
     * propague de forma adecuada un resultado de fallo si la acción subyacente determinó un error.
     */
    @Test
    fun `invoke llama al repositorio pero devuelve un resultado fallido por excepcion`() = runTest {
        val mockBitmap = mockk<Bitmap>()
        val text = "test_text"
        val exception = Exception("Failed to save QR")
        
        coEvery { repository.saveQrCode(mockBitmap, text) } returns Result.failure(exception)

        val result = useCase(mockBitmap, text)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
