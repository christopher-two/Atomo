package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.MenuDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.DishDto
import org.override.atomo.data.remote.dto.MenuCategoryDto
import org.override.atomo.data.remote.dto.MenuDto
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory
import org.override.atomo.domain.repository.MenuRepository

class MenuRepositoryImpl(
    private val menuDao: MenuDao,
    private val supabase: SupabaseClient
) : MenuRepository {
    
    override fun getMenusFlow(userId: String): Flow<List<Menu>> {
        return menuDao.getMenusFlow(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getMenus(userId: String): List<Menu> {
        return menuDao.getMenus(userId).map { it.toDomain() }
    }
    
    override suspend fun getMenu(menuId: String): Menu? {
        val menu = menuDao.getMenu(menuId)?.toDomain() ?: return null
        val categories = menuDao.getCategories(menuId).map { it.toDomain() }
        val dishes = menuDao.getDishes(menuId).map { it.toDomain() }
        return menu.copy(categories = categories, dishes = dishes)
    }
    
    override fun getMenuFlow(menuId: String): Flow<Menu?> {
        return combine(
            menuDao.getMenuFlow(menuId),
            menuDao.getCategoriesFlow(menuId),
            menuDao.getDishesFlow(menuId)
        ) { menu, categories, dishes ->
            menu?.toDomain()?.copy(
                categories = categories.map { it.toDomain() },
                dishes = dishes.map { it.toDomain() }
            )
        }
    }
    
    override suspend fun syncMenus(userId: String): Result<List<Menu>> = runCatching {
        val dtos = supabase.from("menus")
            .select { filter { eq("user_id", userId) } }
            .decodeList<MenuDto>()
        
        val entities = dtos.map { it.toEntity() }
        menuDao.insertMenus(entities)
        
        dtos.forEach { menuDto ->
            syncMenuCategories(menuDto.id)
            syncMenuDishes(menuDto.id)
        }
        
        entities.map { it.toDomain() }
    }
    
    private suspend fun syncMenuCategories(menuId: String) {
        val categories = supabase.from("menu_categories")
            .select { filter { eq("menu_id", menuId) } }
            .decodeList<MenuCategoryDto>()
        menuDao.insertCategories(categories.map { it.toEntity() })
    }
    
    private suspend fun syncMenuDishes(menuId: String) {
        val dishes = supabase.from("dishes")
            .select { filter { eq("menu_id", menuId) } }
            .decodeList<DishDto>()
        menuDao.insertDishes(dishes.map { it.toEntity() })
    }
    
    override suspend fun createMenu(menu: Menu): Result<Menu> = runCatching {
        val dto = menu.toDto()
        supabase.from("menus").insert(dto)
        menuDao.insertMenu(menu.toEntity())
        menu
    }
    
    override suspend fun updateMenu(menu: Menu): Result<Menu> = runCatching {
        val dto = menu.toDto()
        supabase.from("menus").upsert(dto)
        menuDao.updateMenu(menu.toEntity())
        menu
    }
    
    override suspend fun deleteMenu(menuId: String): Result<Unit> = runCatching {
        supabase.from("menus").delete { filter { eq("id", menuId) } }
        menuDao.deleteMenuById(menuId)
    }
    
    // Category operations
    override fun getCategoriesFlow(menuId: String): Flow<List<MenuCategory>> {
        return menuDao.getCategoriesFlow(menuId).map { it.map { c -> c.toDomain() } }
    }
    
    override suspend fun createCategory(category: MenuCategory): Result<MenuCategory> = runCatching {
        supabase.from("menu_categories").insert(category.toDto())
        menuDao.insertCategory(category.toEntity())
        category
    }
    
    override suspend fun updateCategory(category: MenuCategory): Result<MenuCategory> = runCatching {
        supabase.from("menu_categories").upsert(category.toDto())
        menuDao.updateCategory(category.toEntity())
        category
    }
    
    override suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        supabase.from("menu_categories").delete { filter { eq("id", categoryId) } }
    }
    
    // Dish operations
    override fun getDishesFlow(menuId: String): Flow<List<Dish>> {
        return menuDao.getDishesFlow(menuId).map { it.map { d -> d.toDomain() } }
    }
    
    override suspend fun createDish(dish: Dish): Result<Dish> = runCatching {
        supabase.from("dishes").insert(dish.toDto())
        menuDao.insertDish(dish.toEntity())
        dish
    }
    
    override suspend fun updateDish(dish: Dish): Result<Dish> = runCatching {
        supabase.from("dishes").upsert(dish.toDto())
        menuDao.updateDish(dish.toEntity())
        dish
    }
    
    override suspend fun deleteDish(dishId: String): Result<Unit> = runCatching {
        supabase.from("dishes").delete { filter { eq("id", dishId) } }
        menuDao.deleteDishById(dishId)
    }
}
