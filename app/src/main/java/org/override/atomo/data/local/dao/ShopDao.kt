package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.ProductCategoryEntity
import org.override.atomo.data.local.entity.ProductEntity
import org.override.atomo.data.local.entity.ShopEntity

@Dao
interface ShopDao {
    
    // Shop operations
    @Query("SELECT * FROM shops WHERE userId = :userId ORDER BY createdAt DESC")
    fun getShopsFlow(userId: String): Flow<List<ShopEntity>>
    
    @Query("SELECT * FROM shops WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getShops(userId: String): List<ShopEntity>
    
    @Query("SELECT * FROM shops WHERE id = :shopId")
    suspend fun getShop(shopId: String): ShopEntity?
    
    @Query("SELECT * FROM shops WHERE id = :shopId")
    fun getShopFlow(shopId: String): Flow<ShopEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(shops: List<ShopEntity>)
    
    @Update
    suspend fun updateShop(shop: ShopEntity)
    
    @Delete
    suspend fun deleteShop(shop: ShopEntity)
    
    @Query("DELETE FROM shops WHERE id = :shopId")
    suspend fun deleteShopById(shopId: String)
    
    // Category operations
    @Query("SELECT * FROM product_categories WHERE shopId = :shopId ORDER BY sortOrder ASC")
    fun getCategoriesFlow(shopId: String): Flow<List<ProductCategoryEntity>>
    
    @Query("SELECT * FROM product_categories WHERE shopId = :shopId ORDER BY sortOrder ASC")
    suspend fun getCategories(shopId: String): List<ProductCategoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: ProductCategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<ProductCategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: ProductCategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: ProductCategoryEntity)
    
    // Product operations
    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getProductsFlow(shopId: String): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY createdAt DESC")
    suspend fun getProducts(shopId: String): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    suspend fun getProductsByCategory(categoryId: String): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProduct(productId: String): ProductEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: String)
    
    @Query("DELETE FROM products WHERE shopId = :shopId")
    suspend fun deleteProductsByShopId(shopId: String)
    
    @Query("DELETE FROM product_categories WHERE shopId = :shopId")
    suspend fun deleteCategoriesByShopId(shopId: String)
    
    @Query("DELETE FROM shops WHERE userId = :userId")
    suspend fun deleteAllShopsByUser(userId: String)
}
