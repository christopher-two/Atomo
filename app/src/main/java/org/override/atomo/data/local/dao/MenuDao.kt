/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.DishEntity
import org.override.atomo.data.local.entity.MenuCategoryEntity
import org.override.atomo.data.local.entity.MenuEntity

@Dao
interface MenuDao {
    
    // Menu operations
    @Query("SELECT * FROM menus WHERE userId = :userId ORDER BY createdAt DESC")
    fun getMenusFlow(userId: String): Flow<List<MenuEntity>>
    
    @Query("SELECT * FROM menus WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getMenus(userId: String): List<MenuEntity>
    
    @Query("SELECT * FROM menus WHERE id = :menuId")
    suspend fun getMenu(menuId: String): MenuEntity?
    
    @Query("SELECT * FROM menus WHERE id = :menuId")
    fun getMenuFlow(menuId: String): Flow<MenuEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenu(menu: MenuEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenus(menus: List<MenuEntity>)
    
    @Update
    suspend fun updateMenu(menu: MenuEntity)
    
    @Delete
    suspend fun deleteMenu(menu: MenuEntity)
    
    @Query("DELETE FROM menus WHERE id = :menuId")
    suspend fun deleteMenuById(menuId: String)
    
    @Query("DELETE FROM menus WHERE userId = :userId")
    suspend fun deleteAllMenusByUser(userId: String)
    
    // Category operations
    @Query("SELECT * FROM menu_categories WHERE menuId = :menuId ORDER BY sortOrder ASC")
    fun getCategoriesFlow(menuId: String): Flow<List<MenuCategoryEntity>>
    
    @Query("SELECT * FROM menu_categories WHERE menuId = :menuId ORDER BY sortOrder ASC")
    suspend fun getCategories(menuId: String): List<MenuCategoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: MenuCategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<MenuCategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: MenuCategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: MenuCategoryEntity)
    
    @Query("DELETE FROM menu_categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: String)
    
    // Dish operations
    @Query("SELECT * FROM dishes WHERE menuId = :menuId ORDER BY sortOrder ASC")
    fun getDishesFlow(menuId: String): Flow<List<DishEntity>>
    
    @Query("SELECT * FROM dishes WHERE menuId = :menuId ORDER BY sortOrder ASC")
    suspend fun getDishes(menuId: String): List<DishEntity>
    
    @Query("SELECT * FROM dishes WHERE categoryId = :categoryId ORDER BY sortOrder ASC")
    suspend fun getDishesByCategory(categoryId: String): List<DishEntity>
    
    @Query("SELECT * FROM dishes WHERE id = :dishId")
    suspend fun getDish(dishId: String): DishEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDish(dish: DishEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishes(dishes: List<DishEntity>)
    
    @Update
    suspend fun updateDish(dish: DishEntity)
    
    @Delete
    suspend fun deleteDish(dish: DishEntity)
    
    @Query("DELETE FROM dishes WHERE id = :dishId")
    suspend fun deleteDishById(dishId: String)
    
    @Query("DELETE FROM dishes WHERE menuId = :menuId")
    suspend fun deleteDishesByMenuId(menuId: String)
    
    @Query("DELETE FROM menu_categories WHERE menuId = :menuId")
    suspend fun deleteCategoriesByMenuId(menuId: String)
}
