package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.ShopDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.ProductCategoryDto
import org.override.atomo.data.remote.dto.ProductDto
import org.override.atomo.data.remote.dto.ShopDto
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.repository.ShopRepository

class ShopRepositoryImpl(
    private val shopDao: ShopDao,
    private val supabase: SupabaseClient
) : ShopRepository {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getShopsFlow(userId: String): Flow<List<Shop>> {
        return shopDao.getShopsFlow(userId).flatMapLatest { shopEntities ->
            if (shopEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    shopEntities.map { shop ->
                        combine(
                            shopDao.getCategoriesFlow(shop.id),
                            shopDao.getProductsFlow(shop.id)
                        ) { categories, products ->
                            shop.toDomain().copy(
                                categories = categories.map { it.toDomain() },
                                products = products.map { it.toDomain() }
                            )
                        }
                    }
                ) { it.toList() }
            }
        }
    }
    
    override suspend fun getShops(userId: String): List<Shop> {
        return shopDao.getShops(userId).map { it.toDomain() }
    }
    
    override suspend fun getShop(shopId: String): Shop? {
        val shop = shopDao.getShop(shopId)?.toDomain() ?: return null
        val categories = shopDao.getCategories(shopId).map { it.toDomain() }
        val products = shopDao.getProducts(shopId).map { it.toDomain() }
        return shop.copy(categories = categories, products = products)
    }
    
    override fun getShopFlow(shopId: String): Flow<Shop?> {
        return combine(
            shopDao.getShopFlow(shopId),
            shopDao.getCategoriesFlow(shopId),
            shopDao.getProductsFlow(shopId)
        ) { shop, categories, products ->
            shop?.toDomain()?.copy(
                categories = categories.map { it.toDomain() },
                products = products.map { it.toDomain() }
            )
        }
    }
    
    override suspend fun syncShops(userId: String): Result<List<Shop>> = runCatching {
        val dtos = supabase.from("shops")
            .select { filter { eq("user_id", userId) } }
            .decodeList<ShopDto>()
        
        val entities = dtos.map { it.toEntity() }
        shopDao.insertShops(entities)
        
        dtos.forEach { dto ->
            syncShopCategories(dto.id)
            syncShopProducts(dto.id)
        }
        
        entities.map { it.toDomain() }
    }
    
    private suspend fun syncShopCategories(shopId: String) {
        val categories = supabase.from("product_categories")
            .select { filter { eq("shop_id", shopId) } }
            .decodeList<ProductCategoryDto>()
        shopDao.insertCategories(categories.map { it.toEntity() })
    }
    
    private suspend fun syncShopProducts(shopId: String) {
        val products = supabase.from("products")
            .select { filter { eq("shop_id", shopId) } }
            .decodeList<ProductDto>()
        shopDao.insertProducts(products.map { it.toEntity() })
    }
    
    override suspend fun createShop(shop: Shop): Result<Shop> = runCatching {
        supabase.from("shops").insert(shop.toDto())
        shopDao.insertShop(shop.toEntity())
        shop
    }
    
    override suspend fun updateShop(shop: Shop): Result<Shop> = runCatching {
        supabase.from("shops").update(shop.toDto()) {
            filter { eq("id", shop.id) }
        }
        shopDao.updateShop(shop.toEntity())
        shop
    }
    
    override suspend fun deleteShop(shopId: String): Result<Unit> = runCatching {
        supabase.from("shops").delete { filter { eq("id", shopId) } }
        shopDao.deleteShopById(shopId)
    }
    
    override fun getCategoriesFlow(shopId: String): Flow<List<ProductCategory>> {
        return shopDao.getCategoriesFlow(shopId).map { it.map { c -> c.toDomain() } }
    }
    
    override suspend fun createCategory(category: ProductCategory): Result<ProductCategory> = runCatching {
        supabase.from("product_categories").insert(category)
        shopDao.insertCategory(category.toEntity())
        category
    }
    
    override suspend fun updateCategory(category: ProductCategory): Result<ProductCategory> = runCatching {
        supabase.from("product_categories").update(category) {
            filter { eq("id", category.id) }
        }
        shopDao.updateCategory(category.toEntity())
        category
    }
    
    override suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        supabase.from("product_categories").delete { filter { eq("id", categoryId) } }
    }
    
    override fun getProductsFlow(shopId: String): Flow<List<Product>> {
        return shopDao.getProductsFlow(shopId).map { it.map { p -> p.toDomain() } }
    }
    
    override suspend fun createProduct(product: Product): Result<Product> = runCatching {
        supabase.from("products").insert(product.toDto())
        shopDao.insertProduct(product.toEntity())
        product
    }
    
    override suspend fun updateProduct(product: Product): Result<Product> = runCatching {
        supabase.from("products").update(product.toDto()) {
            filter { eq("id", product.id) }
        }
        shopDao.updateProduct(product.toEntity())
        product
    }
    
    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        supabase.from("products").delete { filter { eq("id", productId) } }
        shopDao.deleteProductById(productId)
    }
}
