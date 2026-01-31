/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.menu

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.domain.usecase.subscription.CanAddItemResult
import org.override.atomo.libs.image.api.ImageManager
import java.util.UUID

/**
 * Wrapper for all Menu-related use cases.
 *
 * @property getMenus Retrieves all Menus for a user.
 * @property getMenu Retrieves a single Menu by ID.
 * @property syncMenus Synchronizes Menus from the backend.
 * @property createMenu Creates a new Menu.
 * @property updateMenu Updates an existing Menu.
 * @property deleteMenu Deletes a Menu.
 * @property createCategory Creates a category.
 * @property createDish Creates a dish.
 * @property updateDish Updates a dish.
 * @property deleteDish Deletes a dish.
 */
data class MenuUseCases(
    val getMenus: GetMenusUseCase,
    val getMenu: GetMenuUseCase,
    val syncMenus: SyncMenusUseCase,
    val createMenu: CreateMenuUseCase,
    val updateMenu: UpdateMenuUseCase,
    val deleteMenu: DeleteMenuUseCase,
    val createCategory: CreateCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val createDish: CreateDishUseCase,
    val upsertDish: UpsertDishUseCase,
    val updateDish: UpdateDishUseCase,
    val deleteDish: DeleteDishUseCase
)

/** Retrieves all menus for a user as a Flow. */
class GetMenusUseCase(private val repository: MenuRepository) {
    operator fun invoke(userId: String): Flow<List<Menu>> = repository.getMenusFlow(userId)
}

/** Retrieves a single menu by ID as a Flow. */
class GetMenuUseCase(private val repository: MenuRepository) {
    operator fun invoke(menuId: String): Flow<Menu?> = repository.getMenuFlow(menuId)
}

/** Synchronizes menus from the server. */
class SyncMenusUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(userId: String): Result<List<Menu>> = repository.syncMenus(userId)
}

/** Creates a new menu. */
class CreateMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menu: Menu): Result<Menu> = repository.createMenu(menu)
}

/** Updates an existing menu. */
class UpdateMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menu: Menu): Result<Menu> = repository.updateMenu(menu)
}

/** Deletes a menu by ID, cleaning up associated resources like dish images. */
class DeleteMenuUseCase(
    private val repository: MenuRepository,
    private val deleteDishImageUseCase: DeleteDishImageUseCase
) {
    suspend operator fun invoke(menuId: String): Result<Unit> {
        // Fetch menu to get dishes
        val menu = repository.getMenu(menuId)
        
        // Delete images for each dish
        menu?.dishes?.forEach { dish ->
            if (dish.imageUrl != null) {
                deleteDishImageUseCase(dish.imageUrl)
            }
        }
        
        return repository.deleteMenu(menuId)
    }
}

/** Creates a new category within a menu. */
class CreateCategoryUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(category: MenuCategory): Result<MenuCategory> = repository.createCategory(category)
}

/** Updates an existing category. */
class UpdateCategoryUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(category: MenuCategory): Result<MenuCategory> = repository.updateCategory(category)
}

/** Deletes a category by ID. */
class DeleteCategoryUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(categoryId: String): Result<Unit> = repository.deleteCategory(categoryId)
}

/** Creates a new dish in a menu. */
class CreateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.createDish(dish)
}

/** Upserts a dish in a menu, handling limits and image processing. */
class UpsertDishUseCase(
    private val repository: MenuRepository,
    private val canAddDishUseCase: CanAddDishUseCase,
    private val uploadDishImage: UploadDishImageUseCase,
    private val imageManager: ImageManager
) {
    suspend operator fun invoke(
        userId: String,
        menuId: String,
        name: String,
        description: String,
        price: Double,
        imageUrl: String?,
        categoryId: String?,
        existingDish: Dish?
    ): Result<Dish> = kotlin.runCatching {
        // 1. Limit Check for NEW dishes
        if (existingDish == null) {
            val canAdd = canAddDishUseCase(userId, menuId)
            if (canAdd is CanAddItemResult.LimitReached) {
                throw Exception("Limit reached: ${canAdd.limit} dishes.")
            } else if (canAdd is CanAddItemResult.Error) {
                throw Exception(canAdd.message)
            }
        }

        // 2. Image Processing & Upload
        val finalImageUrl = if (imageUrl != null && imageUrl.startsWith("content://")) {
            val uri = Uri.parse(imageUrl)
            val bytes = imageManager.compressImage(uri).getOrThrow()
            val dishId = existingDish?.id ?: UUID.randomUUID().toString()
            uploadDishImage(userId, dishId, bytes).getOrThrow()
        } else {
            imageUrl
        }

        // 3. Entity Building
        val dishToSave = Dish(
            id = existingDish?.id ?: UUID.randomUUID().toString(),
            menuId = menuId,
            categoryId = categoryId,
            name = name,
            description = description,
            price = price,
            imageUrl = finalImageUrl,
            isVisible = true,
            sortOrder = existingDish?.sortOrder ?: 0,
            createdAt = existingDish?.createdAt ?: System.currentTimeMillis()
        )

        // 4. Persistence
        repository.upsertDish(dishToSave).getOrThrow()
    }
}

/** Updates an existing dish. */
class UpdateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.updateDish(dish)
}

/** Deletes a dish by ID, cleaning up its image if it exists. */
class DeleteDishUseCase(
    private val repository: MenuRepository,
    private val deleteDishImageUseCase: DeleteDishImageUseCase
) {
    suspend operator fun invoke(dish: Dish): Result<Unit> {
        if (dish.imageUrl != null) {
            deleteDishImageUseCase(dish.imageUrl)
        }
        return repository.deleteDish(dish.id)
    }
}
