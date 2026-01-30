/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory

/**
 * Repository interface for managing Menus, Categories, and Dishes.
 */
interface MenuRepository {
    // Menu operations

    /** Retrieves all menus for a user as a Flow. */
    fun getMenusFlow(userId: String): Flow<List<Menu>>

    /** Retrieves all menus for a user (suspend). */
    suspend fun getMenus(userId: String): List<Menu>

    /** Retrieves a single menu by ID (suspend). */
    suspend fun getMenu(menuId: String): Menu?

    /** Retrieves a single menu by ID as a Flow. */
    fun getMenuFlow(menuId: String): Flow<Menu?>

    /** Synchronizes menus from the remote data source. */
    suspend fun syncMenus(userId: String): Result<List<Menu>>

    /** Creates a new menu. */
    suspend fun createMenu(menu: Menu): Result<Menu>

    /** Updates an existing menu. */
    suspend fun updateMenu(menu: Menu): Result<Menu>

    /** Deletes a menu. */
    suspend fun deleteMenu(menuId: String): Result<Unit>

    /** Synchronizes local changes to the remote data source. */
    suspend fun syncUp(userId: String): Result<Unit>

    
    // Category operations

    /** Retrieves categories for a menu as a Flow. */
    fun getCategoriesFlow(menuId: String): Flow<List<MenuCategory>>

    /** Creates a new category. */
    suspend fun createCategory(category: MenuCategory): Result<MenuCategory>

    /** Updates a category. */
    suspend fun updateCategory(category: MenuCategory): Result<MenuCategory>

    /** Deletes a category. */
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    
    // Dish operations

    /** Retrieves dishes for a menu as a Flow. */
    fun getDishesFlow(menuId: String): Flow<List<Dish>>

    /** Creates a new dish. */
    suspend fun createDish(dish: Dish): Result<Dish>

    /** Upserts a dish (creates if not exists, updates if exists). */
    suspend fun upsertDish(dish: Dish): Result<Dish>

    /** Updates a dish. */
    suspend fun updateDish(dish: Dish): Result<Dish>

    /** Deletes a dish. */
    suspend fun deleteDish(dishId: String): Result<Unit>
}
