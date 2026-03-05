/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.storage.domain.usecase.storage

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.storage.domain.repository.StorageRepository

class StorageUseCasesTest {

    private lateinit var repository: StorageRepository
    private lateinit var uploadDishImageUseCase: UploadDishImageUseCase
    private lateinit var deleteDishImageUseCase: DeleteDishImageUseCase

    @Before
    fun setUp() {
        repository = mockk()
        uploadDishImageUseCase = UploadDishImageUseCase(repository)
        deleteDishImageUseCase = DeleteDishImageUseCase(repository)
    }

    /**
     * Verifica que el caso de uso para subir la imagen de un platillo interactúe con el repositorio
     * y devuelva la URL final de la imagen ante un escenario de éxito.
     */
    @Test
    fun `UploadDishImageUseCase llama al repositorio y devuelve la url generada`() = runTest {
        val userId = "user123"
        val dishId = "dish123"
        val byteArray = ByteArray(10)
        val expectedUrl = "http://example.com/image.jpg"
        
        coEvery { 
            repository.uploadImage(userId, "dish", "$dishId.jpg", byteArray) 
        } returns Result.success(expectedUrl)

        val result = uploadDishImageUseCase(userId, dishId, byteArray)
        assertTrue(result.isSuccess)
        assertEquals(expectedUrl, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso para borrar imágenes delegue tal operación al
     * repositorio local sin anomalías.
     */
    @Test
    fun `DeleteDishImageUseCase llama al repositorio y devuelve Unit en exito`() = runTest {
        val imageUrl = "http://example.com/image.jpg"
        
        coEvery { repository.deleteImage(imageUrl) } returns Result.success(Unit)

        val result = deleteDishImageUseCase(imageUrl)
        assertTrue(result.isSuccess)
    }
}
