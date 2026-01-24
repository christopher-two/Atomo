package org.override.atomo.domain.usecase.menu

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory
import org.override.atomo.domain.repository.MenuRepository

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

class GetMenusUseCase(private val repository: MenuRepository) {
    operator fun invoke(userId: String): Flow<List<Menu>> = repository.getMenusFlow(userId)
}

class GetMenuUseCase(private val repository: MenuRepository) {
    operator fun invoke(menuId: String): Flow<Menu?> = repository.getMenuFlow(menuId)
}

class SyncMenusUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(userId: String): Result<List<Menu>> = repository.syncMenus(userId)
}

class CreateMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menu: Menu): Result<Menu> = repository.createMenu(menu)
}

class UpdateMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menu: Menu): Result<Menu> = repository.updateMenu(menu)
}

class DeleteMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(menuId: String): Result<Unit> = repository.deleteMenu(menuId)
}

class CreateCategoryUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(category: MenuCategory): Result<MenuCategory> = repository.createCategory(category)
}

class CreateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.createDish(dish)
}

class UpdateDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dish: Dish): Result<Dish> = repository.updateDish(dish)
}

class DeleteDishUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(dishId: String): Result<Unit> = repository.deleteDish(dishId)
}
