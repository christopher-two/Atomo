/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.domain.usecase.menu

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.model.MenuCategory
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.image.domain.repository.ImageManager
import org.override.atomo.feature.storage.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.feature.storage.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanAddItemResult

class MenuUseCasesTest {

    private lateinit var repository: MenuRepository
    private lateinit var deleteDishImageUseCase: DeleteDishImageUseCase
    private lateinit var canAddDishUseCase: CanAddDishUseCase
    private lateinit var uploadDishImageUseCase: UploadDishImageUseCase
    private lateinit var imageManager: ImageManager

    @Before
    fun setUp() {
        repository = mockk()
        deleteDishImageUseCase = mockk()
        canAddDishUseCase = mockk()
        uploadDishImageUseCase = mockk()
        imageManager = mockk()
    }

    /**
     * Verifica que el caso de uso para obtener menús devuelva el flujo esperado
     * con la lista de menús desde el repositorio.
     */
    @Test
    fun `GetMenusUseCase devuelve el flujo esperado de menus`() = runTest {
        val useCase = GetMenusUseCase(repository)
        val mockMenus = listOf(mockk<Menu>(), mockk<Menu>())
        every { repository.getMenusFlow("user123") } returns flowOf(mockMenus)

        useCase("user123").test {
            assertEquals(mockMenus, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso de uso para obtener un solo menú devuelva el flujo esperado
     * con los datos de un menú en específico.
     */
    @Test
    fun `GetMenuUseCase devuelve el flujo esperado de un menu en especifico`() = runTest {
        val useCase = GetMenuUseCase(repository)
        val mockMenu = mockk<Menu>()
        every { repository.getMenuFlow("menu123") } returns flowOf(mockMenu)

        useCase("menu123").test {
            assertEquals(mockMenu, awaitItem())
            awaitComplete()
        }
    }

    /**
     * Verifica que el caso de uso de sincronización llame al repositorio
     * y devuelva un resultado exitoso con los menús del usuario.
     */
    @Test
    fun `SyncMenusUseCase llama al repositorio y devuelve un resultado exitoso`() = runTest {
        val useCase = SyncMenusUseCase(repository)
        val mockMenus = listOf(mockk<Menu>())
        coEvery { repository.syncMenus("user123") } returns Result.success(mockMenus)

        val result = useCase("user123")
        assertTrue(result.isSuccess)
        assertEquals(mockMenus, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso para crear un menú ejecute la operación en el
     * repositorio y devuelva un resultado exitoso con el menú creado.
     */
    @Test
    fun `CreateMenuUseCase llama al repositorio y devuelve el menu original`() = runTest {
        val useCase = CreateMenuUseCase(repository)
        val mockMenu = mockk<Menu>()
        coEvery { repository.createMenu(mockMenu) } returns Result.success(mockMenu)

        val result = useCase(mockMenu)
        assertTrue(result.isSuccess)
        assertEquals(mockMenu, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso para actualizar un menú ejecute la operación
     * en el repositorio y devuelva el menú actualizado como un resultado exitoso.
     */
    @Test
    fun `UpdateMenuUseCase llama al repositorio para modificar un menu devolviendo el exito`() = runTest {
        val useCase = UpdateMenuUseCase(repository)
        val mockMenu = mockk<Menu>()
        coEvery { repository.updateMenu(mockMenu) } returns Result.success(mockMenu)

        val result = useCase(mockMenu)
        assertTrue(result.isSuccess)
        assertEquals(mockMenu, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso al borrar un menú también elimine las
     * imágenes asociadas a cada platillo que contenía antes de borrar el menú en sí.
     */
    @Test
    fun `DeleteMenuUseCase borra el menu y limpia las imagenes de sus platillos`() = runTest {
        val useCase = DeleteMenuUseCase(repository, deleteDishImageUseCase)
        
        val mockDish1 = mockk<Dish>()
        every { mockDish1.imageUrl } returns "url1"
        val mockDish2 = mockk<Dish>()
        every { mockDish2.imageUrl } returns null
        val mockMenu = mockk<Menu>()
        every { mockMenu.dishes } returns listOf(mockDish1, mockDish2)
        
        coEvery { repository.getMenu("menu123") } returns mockMenu
        coEvery { deleteDishImageUseCase("url1") } returns Result.success(Unit)
        coEvery { repository.deleteMenu("menu123") } returns Result.success(Unit)

        val result = useCase("menu123")
        assertTrue(result.isSuccess)
        
        coVerify(exactly = 1) { deleteDishImageUseCase("url1") }
        coVerify(exactly = 1) { repository.deleteMenu("menu123") }
    }

    /**
     * Verifica que el caso de uso para crear una categoría llame al repositorio
     * devolviendo el modelo de categoría dentro de un resultado exitoso.
     */
    @Test
    fun `CreateCategoryUseCase llama al repositorio exitosamente al guardar`() = runTest {
        val useCase = CreateCategoryUseCase(repository)
        val mockCategory = mockk<MenuCategory>()
        coEvery { repository.createCategory(mockCategory) } returns Result.success(mockCategory)

        val result = useCase(mockCategory)
        assertTrue(result.isSuccess)
        assertEquals(mockCategory, result.getOrNull())
    }

    /**
     * Verifica que el caso de uso para actualizar una categoría se ejecute a través
     * del repositorio devolviendo la misma entidad editada.
     */
    @Test
    fun `UpdateCategoryUseCase modifica la categoria satisfactoriamente mediante el respositorio`() = runTest {
        val useCase = UpdateCategoryUseCase(repository)
        val mockCategory = mockk<MenuCategory>()
        coEvery { repository.updateCategory(mockCategory) } returns Result.success(mockCategory)

        val result = useCase(mockCategory)
        assertTrue(result.isSuccess)
        assertEquals(mockCategory, result.getOrNull())
    }

    /**
     * Verifica que eliminar una categoría funcione interactuando con el repositorio
     * y arrojando Unit determinando el éxito de la operación.
     */
    @Test
    fun `DeleteCategoryUseCase borra la categoria validando la operacion exitosa en el repositorio`() = runTest {
        val useCase = DeleteCategoryUseCase(repository)
        coEvery { repository.deleteCategory("cat123") } returns Result.success(Unit)

        val result = useCase("cat123")
        assertTrue(result.isSuccess)
    }

    /**
     * Verifica que la función sencilla para la creación de un nuevo platillo sin 
     * validaciones superiores se realice con apoyo del repositorio.
     */
    @Test
    fun `CreateDishUseCase llama al repositorio guardando del platillo`() = runTest {
        val useCase = CreateDishUseCase(repository)
        val mockDish = mockk<Dish>()
        coEvery { repository.createDish(mockDish) } returns Result.success(mockDish)

        val result = useCase(mockDish)
        assertTrue(result.isSuccess)
        assertEquals(mockDish, result.getOrNull())
    }

    /**
     * Verifica que sea posible actualizar los detalles de un platillo validando la iteración con 
     * el repositorio mediante este caso de uso intermedio.
     */
    @Test
    fun `UpdateDishUseCase modifica satisfactoriamente una entidad dish a base del repositorio`() = runTest {
        val useCase = UpdateDishUseCase(repository)
        val mockDish = mockk<Dish>()
        coEvery { repository.updateDish(mockDish) } returns Result.success(mockDish)

        val result = useCase(mockDish)
        assertTrue(result.isSuccess)
        assertEquals(mockDish, result.getOrNull())
    }

    /**
     * Verifica el proceso de borrado de un platillo determinando si 
     * borra en consecuencia las imágenes generadas por el mismo si cuentan con URL real de Storage
     * validando interacciones con los servicios.
     */
    @Test
    fun `DeleteDishUseCase borra la imagen si el URL esta presente y antes de borrar la definicion de base de datos`() = runTest {
        val useCase = DeleteDishUseCase(repository, deleteDishImageUseCase)
        val mockDish = mockk<Dish>()
        every { mockDish.id } returns "dish123"
        every { mockDish.imageUrl } returns "url_image"
        
        coEvery { deleteDishImageUseCase("url_image") } returns Result.success(Unit)
        coEvery { repository.deleteDish("dish123") } returns Result.success(Unit)

        val result = useCase(mockDish)
        assertTrue(result.isSuccess)
        
        coVerify(exactly = 1) { deleteDishImageUseCase("url_image") }
        coVerify(exactly = 1) { repository.deleteDish("dish123") }
    }
    
    /**
     * Valida el proceso robusto de la configuración y actualización de platillos,
     * determinando que lanza excepciones válidas si el límite global ha fallado la retención por subscripción y estado
     * al ser un ítem enteramente nuevo.
     */
    @Test
    fun `UpsertDishUseCase arroja una excepcion si limite por subscripcion fue cruzado por platillos de nueva creacion`() = runTest {
        val useCase = UpsertDishUseCase(repository, canAddDishUseCase, uploadDishImageUseCase, imageManager)
        
        coEvery { canAddDishUseCase("userId", "menuId") } returns CanAddItemResult.LimitReached(10, 10)
        
        val result = useCase(
            userId = "userId",
            menuId = "menuId",
            name = "name",
            description = "desc",
            price = 10.0,
            imageUrl = null,
            categoryId = null,
            existingDish = null
        )
        
        assertTrue(result.isFailure)
        assertEquals("Limit reached: 10 dishes.", result.exceptionOrNull()?.message)
    }
    
    /**
     * Valida la creacion o actualizacion con éxito desde la llamada de un platillo asumiendo que el url sea ajeno
     * al dispositivo previniendo el escalamiento de parseo de imagen por un proceso de carga simple
     * que finaliza ejecutando unit o un Dish en estado success base sin compresiones generadas.
     */
    @Test
    fun `UpsertDishUseCase finaliza satisfactoriamente ante un platillo sin necesidad de procesos de imagen local`() = runTest {
        val useCase = UpsertDishUseCase(repository, canAddDishUseCase, uploadDishImageUseCase, imageManager)
        
        coEvery { canAddDishUseCase("userId", "menuId") } returns CanAddItemResult.Success
        val mockDish = mockk<Dish>()
        coEvery { repository.upsertDish(any()) } returns Result.success(mockDish)
        
        val result = useCase(
            userId = "userId",
            menuId = "menuId",
            name = "name",
            description = "desc",
            price = 10.0,
            imageUrl = "http://existing.url.com", // Not content:// so no processing
            categoryId = null,
            existingDish = null
        )
        
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.upsertDish(any()) }
        coVerify(exactly = 0) { imageManager.compressImage(any()) }
    }
}
