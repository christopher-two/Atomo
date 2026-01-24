package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory

interface MenuRepository {
    // Menu operations
    fun getMenusFlow(userId: String): Flow<List<Menu>>
    suspend fun getMenus(userId: String): List<Menu>
    suspend fun getMenu(menuId: String): Menu?
    fun getMenuFlow(menuId: String): Flow<Menu?>
    suspend fun syncMenus(userId: String): Result<List<Menu>>
    suspend fun createMenu(menu: Menu): Result<Menu>
    suspend fun updateMenu(menu: Menu): Result<Menu>
    suspend fun deleteMenu(menuId: String): Result<Unit>
    
    // Category operations
    fun getCategoriesFlow(menuId: String): Flow<List<MenuCategory>>
    suspend fun createCategory(category: MenuCategory): Result<MenuCategory>
    suspend fun updateCategory(category: MenuCategory): Result<MenuCategory>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    
    // Dish operations
    fun getDishesFlow(menuId: String): Flow<List<Dish>>
    suspend fun createDish(dish: Dish): Result<Dish>
    suspend fun updateDish(dish: Dish): Result<Dish>
    suspend fun deleteDish(dishId: String): Result<Unit>
}
