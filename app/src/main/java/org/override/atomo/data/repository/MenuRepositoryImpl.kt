/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

/**
 * Implementation of [MenuRepository] using [MenuDao] and [SupabaseClient].
 * Handles data synchronization between local Room database and remote Supabase instance.
 */
class MenuRepositoryImpl(
    private val menuDao: MenuDao,
    private val supabase: SupabaseClient,
    private val syncManager: org.override.atomo.data.manager.SyncManager
) : MenuRepository {

    // region Menu Flows

    /**
     * Observes all menus for a specific user, combining them with their related categories and dishes.
     *
     * @param userId The ID of the user whose menus are to be observed.
     * @return A Flow emitting a list of [Menu] objects with their hierarchies populated.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getMenusFlow(userId: String): Flow<List<Menu>> {
        return menuDao.getMenusFlow(userId).flatMapLatest { menuEntities ->
            if (menuEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    menuEntities.map { menu ->
                        combine(
                            menuDao.getCategoriesFlow(menu.id),
                            menuDao.getDishesFlow(menu.id)
                        ) { categories, dishes ->
                            menu.toDomain().copy(
                                categories = categories.map { it.toDomain() },
                                dishes = dishes.map { it.toDomain() }
                            )
                        }
                    }
                ) { it.toList() }
            }
        }
    }

    /**
     * Retrieves all menus for a specific user.
     *
     * @param userId The user's unique identifier.
     * @return A list of [Menu] objects.
     */
    override suspend fun getMenus(userId: String): List<Menu> {
        return menuDao.getMenus(userId).map { it.toDomain() }
    }

    /**
     * Retrieves a single menu by its ID, including its categories and dishes.
     *
     * @param menuId The unique identifier of the menu.
     * @return The [Menu] object if found, null otherwise.
     */
    override suspend fun getMenu(menuId: String): Menu? {
        val menu = menuDao.getMenu(menuId)?.toDomain() ?: return null
        val categories = menuDao.getCategories(menuId).map { it.toDomain() }
        val dishes = menuDao.getDishes(menuId).map { it.toDomain() }
        return menu.copy(categories = categories, dishes = dishes)
    }

    /**
     * Observes a single menu by its ID.
     *
     * @param menuId The unique identifier of the menu.
     * @return A Flow emitting the [Menu] object or null if not found.
     */
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
    // endregion

    // region Menu CRUD

    /**
     * Creates a new menu locally and schedules a synchronization upload.
     *
     * @param menu The menu to create.
     * @return A Result containing the created Menu.
     */
    override suspend fun createMenu(menu: Menu): Result<Menu> = performOptimisticUpdate(menu) {
        menuDao.insertMenu(menu.toEntity().copy(isSynced = false))
    }

    /**
     * Updates an existing menu locally and schedules a synchronization upload.
     *
     * @param menu The menu with updated information.
     * @return A Result containing the updated Menu.
     */
    override suspend fun updateMenu(menu: Menu): Result<Menu> = performOptimisticUpdate(menu) {
        menuDao.updateMenu(menu.toEntity().copy(isSynced = false))
    }

    /**
     * Soft deletes a menu locally (isActive=false) and schedules a synchronization upload.
     *
     * @param menuId The ID of the menu to delete.
     * @return A Result indicating success or failure.
     */
    override suspend fun deleteMenu(menuId: String): Result<Unit> = runCatching {
        val menu = menuDao.getMenu(menuId)
        if (menu != null) {
            menuDao.updateMenu(menu.copy(isActive = false, isSynced = false))
            syncManager.scheduleUpload(menu.userId)
        }
    }
    // endregion

    // region Category Flows & CRUD

    /**
     * Observes all categories for a specific menu.
     *
     * @param menuId The ID of the menu.
     * @return A Flow emitting a list of [MenuCategory] objects.
     */
    override fun getCategoriesFlow(menuId: String): Flow<List<MenuCategory>> {
        return menuDao.getCategoriesFlow(menuId).map { it.map { c -> c.toDomain() } }
    }

    /**
     * Creates a new category locally and schedules a synchronization upload.
     *
     * @param category The category to create.
     * @return A Result containing the created MenuCategory.
     */
    override suspend fun createCategory(category: MenuCategory): Result<MenuCategory> =
        performOptimisticUpdate(category) {
        menuDao.insertCategory(category.toEntity().copy(isSynced = false))
    }

    /**
     * Updates an existing category locally and schedules a synchronization upload.
     *
     * @param category The category to update.
     * @return A Result containing the updated MenuCategory.
     */
    override suspend fun updateCategory(category: MenuCategory): Result<MenuCategory> =
        performOptimisticUpdate(category) {
        menuDao.updateCategory(category.toEntity().copy(isSynced = false))
    }

    /**
     * Deletes a category locally and attempts an immediate deletion on the server.
     * Note: This performs a "best effort" remote deletion as categories do not currently support soft delete.
     *
     * @param categoryId The ID of the category to delete.
     * @return A Result indicating success or failure.
     */
    override suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        menuDao.deleteCategoryById(categoryId)
        try {
            supabase.from("menu_categories").delete { filter { eq("id", categoryId) } }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // endregion

    // region Dish Flows & CRUD

    /**
     * Observes all dishes for a specific menu.
     *
     * @param menuId The ID of the menu.
     * @return A Flow emitting a list of [Dish] objects.
     */
    override fun getDishesFlow(menuId: String): Flow<List<Dish>> {
        return menuDao.getDishesFlow(menuId).map { it.map { d -> d.toDomain() } }
    }

    /**
     * Creates a new dish locally and schedules a synchronization upload.
     *
     * @param dish The dish to create.
     * @return A Result containing the created Dish.
     */
    override suspend fun createDish(dish: Dish): Result<Dish> = performOptimisticUpdate(dish) {
        menuDao.insertDish(dish.toEntity().copy(isSynced = false))
    }

    /**
     * Upserts a dish locally and schedules a synchronization upload.
     *
     * @param dish The dish to upsert.
     * @return A Result containing the upserted Dish.
     */
    override suspend fun upsertDish(dish: Dish): Result<Dish> = performOptimisticUpdate(dish) {
        menuDao.insertDish(dish.toEntity().copy(isSynced = false))
    }

    /**
     * Updates an existing dish locally and schedules a synchronization upload.
     *
     * @param dish The dish to update.
     * @return A Result containing the updated Dish.
     */
    override suspend fun updateDish(dish: Dish): Result<Dish> = performOptimisticUpdate(dish) {
        menuDao.updateDish(dish.toEntity().copy(isSynced = false))
    }

    /**
     * Soft deletes a dish locally (isVisible=false) and schedules a synchronization upload.
     *
     * @param dishId The ID of the dish to delete.
     * @return A Result indicating success or failure.
     */
    override suspend fun deleteDish(dishId: String): Result<Unit> = runCatching {
        val dish = menuDao.getDish(dishId)
        if (dish != null) {
            menuDao.updateDish(dish.copy(isVisible = false, isSynced = false))

            val userId = menuDao.getMenu(dish.menuId)?.userId
            if (userId != null) {
                syncManager.scheduleUpload(userId)
            }
        }
    }
    // endregion

    // region Synchronization (Sync Down / Pull)

    /**
     * Synchronizes menus from the server to the local database (Pull).
     * Downloads menus, categories, and dishes. Respects local unsynced changes.
     *
     * @param userId The user ID to sync data for.
     * @return A Result containing the list of synchronized Menus.
     */
    override suspend fun syncMenus(userId: String): Result<List<Menu>> = runCatching {
        syncDownMenus(userId)

        val currentMenus = menuDao.getMenus(userId)
        currentMenus.forEach { menu ->
            syncDownCategories(menu.id)
            syncDownDishes(menu.id)
        }

        currentMenus.map { it.toDomain() }
    }

    /**
     * Downloads and syncs menus from the server used by [syncMenus].
     */
    private suspend fun syncDownMenus(userId: String): List<MenuDto> {
        val dtos = supabase.from("menus")
            .select { filter { eq("user_id", userId) } }
            .decodeList<MenuDto>()

        val localMenus = menuDao.getMenus(userId)
        val remoteMenuIds = dtos.map { it.id }.toSet()

        localMenus.forEach { localMenu ->
            if (localMenu.isSynced && localMenu.id !in remoteMenuIds) {
                menuDao.deleteDishesByMenuId(localMenu.id)
                menuDao.deleteCategoriesByMenuId(localMenu.id)
                menuDao.deleteMenuById(localMenu.id)
            }
        }

        val entitiesToInsert = dtos.mapNotNull { dto ->
            val local = localMenus.find { it.id == dto.id }
            if (local != null && !local.isSynced) {
                null
            } else {
                dto.toEntity().copy(isSynced = true)
            }
        }

        if (entitiesToInsert.isNotEmpty()) {
            menuDao.insertMenus(entitiesToInsert)
        }
        return dtos
    }

    /**
     * Downloads and syncs categories for a specific menu used by [syncMenus].
     */
    private suspend fun syncDownCategories(menuId: String) {
        val remoteCategories = supabase.from("menu_categories")
            .select { filter { eq("menu_id", menuId) } }
            .decodeList<MenuCategoryDto>()

        val localCategories = menuDao.getCategories(menuId)
        val remoteIds = remoteCategories.map { it.id }.toSet()

        localCategories.forEach { local ->
            if (local.isSynced && local.id !in remoteIds) {
                menuDao.deleteCategoryById(local.id)
            }
        }

        val toInsert = remoteCategories.mapNotNull { dto ->
            val local = localCategories.find { it.id == dto.id }
            if (local != null && !local.isSynced) null
            else dto.toEntity().copy(isSynced = true)
        }

        if (toInsert.isNotEmpty()) {
            menuDao.insertCategories(toInsert)
        }
    }

    /**
     * Downloads and syncs dishes for a specific menu used by [syncMenus].
     */
    private suspend fun syncDownDishes(menuId: String) {
        val remoteDishes = supabase.from("dishes")
            .select { filter { eq("menu_id", menuId) } }
            .decodeList<DishDto>()

        val localDishes = menuDao.getDishes(menuId)
        val remoteIds = remoteDishes.map { it.id }.toSet()

        localDishes.forEach { local ->
            if (local.isSynced && local.id !in remoteIds) {
                menuDao.deleteDishById(local.id)
            }
        }

        val toInsert = remoteDishes.mapNotNull { dto ->
            val local = localDishes.find { it.id == dto.id }
            if (local != null && !local.isSynced) null
            else dto.toEntity().copy(isSynced = true)
        }

        if (toInsert.isNotEmpty()) {
            menuDao.insertDishes(toInsert)
        }
    }

    // endregion

    // region Synchronization (Sync Up / Push)

    /**
     * Uploads local unsynced changes to the server (Push).
     * Handles ID conflicts by remapping local IDs to serve IDs if necessary ("One Menu Policy").
     *
     * @param userId The user ID to sync data for.
     * @return A Result indicating success or failure.
     */
    override suspend fun syncUp(userId: String): Result<Unit> = runCatching {
        val remoteMenus = supabase.from("menus")
            .select { filter { eq("user_id", userId) } }
            .decodeList<MenuDto>()

        val existingRemoteMenu = remoteMenus.firstOrNull()
        val idMapping = mutableMapOf<String, String>()

        if (existingRemoteMenu != null) {
            val allLocalMenus = menuDao.getMenus(userId)
            allLocalMenus.forEach { localMenu ->
                if (localMenu.id != existingRemoteMenu.id) {
                    idMapping[localMenu.id] = existingRemoteMenu.id
                }
            }
        }

        syncLocalMenusWithServer(userId, existingRemoteMenu, idMapping)

        syncLocalCategoriesWithServer(userId, idMapping)

        syncLocalDishesWithServer(userId, idMapping)
    }

    /**
     * Syncs local menus with the server, handling ID migration if requested.
     */
    private suspend fun syncLocalMenusWithServer(
        userId: String,
        existingRemoteMenu: MenuDto?,
        idMapping: MutableMap<String, String>
    ) {
        val unsyncedMenus = menuDao.getUnsyncedMenus(userId)

        unsyncedMenus.forEach { entity ->
            var dto = entity.toDomain().toDto()

            if (existingRemoteMenu != null && dto.id != existingRemoteMenu.id) {
                val newMenuId = existingRemoteMenu.id
                val oldMenuId = entity.id

                idMapping[oldMenuId] = newMenuId
                dto = dto.copy(id = newMenuId)

                menuDao.insertMenu(entity.copy(id = newMenuId, isSynced = false))

                val categories = menuDao.getCategories(oldMenuId)
                categories.forEach { menuDao.insertCategory(it.copy(menuId = newMenuId)) }
                menuDao.deleteCategoriesByMenuId(oldMenuId)

                val dishes = menuDao.getDishes(oldMenuId)
                dishes.forEach { menuDao.insertDish(it.copy(menuId = newMenuId)) }
                menuDao.deleteDishesByMenuId(oldMenuId)

                menuDao.deleteMenuById(oldMenuId)

                supabase.from("menus").update(dto) { filter { eq("id", dto.id) } }

                menuDao.updateMenu(entity.copy(id = newMenuId, isSynced = true))

            } else {
                if (existingRemoteMenu != null) {
                    supabase.from("menus").update(dto) { filter { eq("id", dto.id) } }
                } else {
                    supabase.from("menus").upsert(dto) { onConflict = "id" }
                }
                menuDao.insertMenu(entity.copy(isSynced = true))
            }
        }
    }

    /**
     * Syncs local categories with the server, applying any parent menu ID substitutions.
     */
    private suspend fun syncLocalCategoriesWithServer(
        userId: String,
        idMapping: Map<String, String>
    ) {
        val unsyncedCategories = menuDao.getAllUnsyncedCategories()

        unsyncedCategories.forEach { entity ->
            var dto = entity.toDomain().toDto()
            idMapping[dto.menuId]?.let { newMenuId ->
                dto = dto.copy(menuId = newMenuId)
            }

            supabase.from("menu_categories").upsert(dto) { onConflict = "id" }
            menuDao.insertCategory(entity.copy(isSynced = true))
        }
    }

    /**
     * Syncs local dishes with the server, applying any parent menu ID substitutions.
     */
    private suspend fun syncLocalDishesWithServer(userId: String, idMapping: Map<String, String>) {
        val unsyncedDishes = menuDao.getAllUnsyncedDishes()

        unsyncedDishes.forEach { entity ->
            var dto = entity.toDomain().toDto()
            idMapping[dto.menuId]?.let { newMenuId ->
                dto = dto.copy(menuId = newMenuId)
            }

            supabase.from("dishes").upsert(dto) { onConflict = "id" }
            menuDao.insertDish(entity.copy(isSynced = true))
        }
    }
    // endregion

    // region Helpers

    /**
     * Executes an optimistic local update and schedules a sync.
     * Automatically extracts userId from the domain object to trigger sync.
     *
     * @param domainObject The object being modified.
     * @param localOperation A suspend block that executes the local database update.
     * @return A Result wrapping the domain object.
     */
    private suspend fun <T : Any> performOptimisticUpdate(
        domainObject: T,
        localOperation: suspend () -> Unit
    ): Result<T> = runCatching {
        localOperation()

        val userId = when (domainObject) {
            is Menu -> domainObject.userId
            is MenuCategory -> menuDao.getMenu(domainObject.menuId)?.userId
            is Dish -> menuDao.getMenu(domainObject.menuId)?.userId
            else -> null
        }

        if (userId != null) {
            syncManager.scheduleUpload(userId)
        }

        domainObject
    }
    // endregion
}
