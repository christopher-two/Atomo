package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop

interface ShopRepository {
    fun getShopsFlow(userId: String): Flow<List<Shop>>
    suspend fun getShops(userId: String): List<Shop>
    suspend fun getShop(shopId: String): Shop?
    fun getShopFlow(shopId: String): Flow<Shop?>
    suspend fun syncShops(userId: String): Result<List<Shop>>
    suspend fun createShop(shop: Shop): Result<Shop>
    suspend fun updateShop(shop: Shop): Result<Shop>
    suspend fun deleteShop(shopId: String): Result<Unit>
    
    // Category operations
    fun getCategoriesFlow(shopId: String): Flow<List<ProductCategory>>
    suspend fun createCategory(category: ProductCategory): Result<ProductCategory>
    suspend fun updateCategory(category: ProductCategory): Result<ProductCategory>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    
    // Product operations
    fun getProductsFlow(shopId: String): Flow<List<Product>>
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(product: Product): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
