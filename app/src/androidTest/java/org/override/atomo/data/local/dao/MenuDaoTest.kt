/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.data.local.entity.DishEntity
import org.override.atomo.data.local.entity.MenuCategoryEntity
import org.override.atomo.data.local.entity.MenuEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MenuDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var menuDao: MenuDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        menuDao = db.menuDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetMenu() = runTest {
        val menu = createMenu("menu_1", "user_1")
        menuDao.insertMenu(menu)
        
        val loaded = menuDao.getMenu("menu_1")
        assertEquals(menu, loaded)
    }

    @Test
    fun insertAndGetCategories() = runTest {
        val menu = createMenu("menu_2", "user_1")
        menuDao.insertMenu(menu)
        
        val categories = listOf(
            createCategory("cat_1", "menu_2", "Drinks", 0),
            createCategory("cat_2", "menu_2", "Food", 1)
        )
        menuDao.insertCategories(categories)
        
        val loaded = menuDao.getCategories("menu_2")
        assertEquals(2, loaded.size)
        assertEquals("Drinks", loaded[0].name)
    }

    @Test
    fun insertAndGetDishes() = runTest {
        val menu = createMenu("menu_3", "user_1")
        menuDao.insertMenu(menu)
        
        val category = createCategory("cat_3", "menu_3", "Main Course", 0)
        menuDao.insertCategory(category)
        
        val dishes = listOf(
            createDish("dish_1", "menu_3", "cat_3", "Burger", 15.0),
            createDish("dish_2", "menu_3", "cat_3", "Pizza", 12.0)
        )
        menuDao.insertDishes(dishes)
        
        val loaded = menuDao.getDishesByCategory("cat_3")
        assertEquals(2, loaded.size)
        assertTrue(loaded.any { it.name == "Burger" })
    }

    private fun createMenu(id: String, userId: String) = MenuEntity(
        id = id,
        userId = userId,
        name = "Menu $id",
        description = "Description $id",
        logoUrl = null,
        createdAt = System.currentTimeMillis()
    )

    private fun createCategory(id: String, menuId: String, name: String, sortOrder: Int) = MenuCategoryEntity(
        id = id,
        menuId = menuId,
        name = name,
        sortOrder = sortOrder,
        createdAt = System.currentTimeMillis()
    )

    private fun createDish(id: String, menuId: String, categoryId: String, name: String, price: Double) = DishEntity(
        id = id,
        menuId = menuId,
        categoryId = categoryId,
        name = name,
        description = "Delicious $name",
        price = price,
        imageUrl = null,
        createdAt = System.currentTimeMillis()
    )
}
