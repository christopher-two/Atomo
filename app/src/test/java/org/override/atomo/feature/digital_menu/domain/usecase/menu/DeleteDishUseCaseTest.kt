/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.domain.usecase.menu

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.storage.domain.usecase.storage.DeleteDishImageUseCase

class DeleteDishUseCaseTest {

    private lateinit var deleteDishUseCase: DeleteDishUseCase
    private val menuRepository: MenuRepository = mockk()
    private val deleteDishImageUseCase: DeleteDishImageUseCase = mockk()

    private val dishWithImage = Dish(
        id = "dish1",
        menuId = "menu1",
        categoryId = null,
        name = "Pizza",
        description = "Deliciosa pizza",
        price = 120.0,
        imageUrl = "https://example.com/pizza.jpg",
        isVisible = true,
        sortOrder = 0,
        createdAt = 1_000L
    )

    private val dishWithoutImage = dishWithImage.copy(
        id = "dish2",
        name = "Agua",
        imageUrl = null
    )

    @Before
    fun setUp() {
        deleteDishUseCase = DeleteDishUseCase(menuRepository, deleteDishImageUseCase)
    }

    // -------------------------------------------------------------------------
    // Happy path: dish con imagen
    // -------------------------------------------------------------------------
    @Test
    fun `invoke debe eliminar la imagen y luego llamar a deleteDish en el repositorio`() = runTest {
        coEvery { deleteDishImageUseCase(dishWithImage.imageUrl!!) } returns Result.success(Unit)
        coEvery { menuRepository.deleteDish(dishWithImage.id) } returns Result.success(Unit)

        val result = deleteDishUseCase(dishWithImage)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { deleteDishImageUseCase(dishWithImage.imageUrl!!) }
        coVerify(exactly = 1) { menuRepository.deleteDish(dishWithImage.id) }
    }

    // -------------------------------------------------------------------------
    // Happy path: dish sin imagen
    // -------------------------------------------------------------------------
    @Test
    fun `invoke no debe llamar a deleteDishImage cuando el dish no tiene imagen`() = runTest {
        coEvery { menuRepository.deleteDish(dishWithoutImage.id) } returns Result.success(Unit)

        val result = deleteDishUseCase(dishWithoutImage)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { deleteDishImageUseCase(any()) }
        coVerify(exactly = 1) { menuRepository.deleteDish(dishWithoutImage.id) }
    }

    // -------------------------------------------------------------------------
    // Caso límite: el repositorio falla
    // -------------------------------------------------------------------------
    @Test
    fun `invoke debe retornar failure cuando el repositorio falla al eliminar`() = runTest {
        val error = RuntimeException("DB error")
        coEvery { deleteDishImageUseCase(dishWithImage.imageUrl!!) } returns Result.success(Unit)
        coEvery { menuRepository.deleteDish(dishWithImage.id) } returns Result.failure(error)

        val result = deleteDishUseCase(dishWithImage)

        assertFalse(result.isSuccess)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}

