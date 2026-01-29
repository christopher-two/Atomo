package org.override.atomo.domain.usecase.menu

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase

class DeleteMenuUseCaseTest {

    private lateinit var deleteMenuUseCase: DeleteMenuUseCase
    private val menuRepository: MenuRepository = mockk()
    private val deleteDishImageUseCase: DeleteDishImageUseCase = mockk()

    private val testDish = Dish(
        id = "dish1",
        menuId = "menu1",
        categoryId = null,
        name = "Pizza",
        description = null,
        price = 10.0,
        imageUrl = "http://example.com/image.jpg",
        isVisible = true,
        sortOrder = 0,
        createdAt = 1000L
    )

    private val testMenu = Menu(
        id = "menu1",
        userId = "user1",
        name = "Menu",
        description = null,
        isActive = true,
        templateId = "t1",
        primaryColor = "#000",
        fontFamily = "Arial",
        logoUrl = null,
        createdAt = 1000L,
        dishes = listOf(testDish)
    )

    @Before
    fun setUp() {
        deleteMenuUseCase = DeleteMenuUseCase(menuRepository, deleteDishImageUseCase)
    }

    @Test
    fun `invoke should delete dish images before deleting menu`() = runTest {
        // Arrange
        coEvery { menuRepository.getMenu("menu1") } returns testMenu
        coEvery { deleteDishImageUseCase("http://example.com/image.jpg") } returns Result.success(Unit)
        coEvery { menuRepository.deleteMenu("menu1") } returns Result.success(Unit)

        // Act
        val result = deleteMenuUseCase("menu1")

        // Assert
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { deleteDishImageUseCase("http://example.com/image.jpg") }
        coVerify(exactly = 1) { menuRepository.deleteMenu("menu1") }
    }
}
