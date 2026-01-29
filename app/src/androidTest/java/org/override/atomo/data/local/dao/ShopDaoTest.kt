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
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.data.local.entity.ProductCategoryEntity
import org.override.atomo.data.local.entity.ProductEntity
import org.override.atomo.data.local.entity.ProfileEntity
import org.override.atomo.data.local.entity.ShopEntity
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShopDaoTest {

    private lateinit var db: AtomoDatabase
    private lateinit var shopDao: ShopDao
    private lateinit var profileDao: ProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AtomoDatabase::class.java
        ).build()
        shopDao = db.shopDao()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetShop() = runTest {
        val profile = createProfile("user_1")
        profileDao.insertProfile(profile)
        
        val shop = createShop("shop_1", "user_1")
        shopDao.insertShop(shop)
        
        val loaded = shopDao.getShop("shop_1")
        assertEquals(shop, loaded)
    }

    @Test
    fun insertAndGetProducts() = runTest {
        val profile = createProfile("user_2")
        profileDao.insertProfile(profile)
        
        val shop = createShop("shop_2", "user_2")
        shopDao.insertShop(shop)
        
        val category = createCategory("cat_1", "shop_2", "Electronics")
        shopDao.insertCategory(category)
        
        val product = createProduct("prod_1", "shop_2", "cat_1", "Phone", 500.0)
        shopDao.insertProduct(product)
        
        val loaded = shopDao.getProductsByCategory("cat_1")
        assertEquals(1, loaded.size)
        assertEquals("Phone", loaded[0].name)
    }

    private fun createProfile(id: String) = ProfileEntity(
        id = id,
        username = "user_$id",
        displayName = "User $id",
        avatarUrl = null,
        socialLinks = null,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun createShop(id: String, userId: String) = ShopEntity(
        id = id,
        userId = userId,
        name = "Shop $id",
        description = "Welcome to Shop $id",
        createdAt = System.currentTimeMillis()
    )

    private fun createCategory(id: String, shopId: String, name: String) = ProductCategoryEntity(
        id = id,
        shopId = shopId,
        name = name,
        createdAt = System.currentTimeMillis()
    )

    private fun createProduct(id: String, shopId: String, categoryId: String, name: String, price: Double) = ProductEntity(
        id = id,
        shopId = shopId,
        categoryId = categoryId,
        name = name,
        description = "Excellent $name",
        price = price,
        imageUrl = null,
        createdAt = System.currentTimeMillis()
    )
}
