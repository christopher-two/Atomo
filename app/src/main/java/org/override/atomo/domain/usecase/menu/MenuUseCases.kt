/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.menu

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory
import org.override.atomo.domain.repository.MenuRepository

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
    val createDish: CreateDishUseCase,
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

/** Deletes a menu by ID. */
class DeleteMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menuId: String): Result<Unit> = repository.deleteMenu(menuId)
}

/** Creates a new category within a menu. */
class CreateCategoryUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(category: MenuCategory): Result<MenuCategory> = repository.createCategory(category)
}

/** Creates a new dish in a menu. */
class CreateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.createDish(dish)
}

/** Updates an existing dish. */
class UpdateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.updateDish(dish)
}

/** Deletes a dish by ID. */
class DeleteDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dishId: String): Result<Unit> = repository.deleteDish(dishId)
}
