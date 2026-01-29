/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop

/**
 * Repository interface for managing Shops, Categories, and Products.
 */
interface ShopRepository {
    /** Retrieves all shops for a user as a Flow. */
    fun getShopsFlow(userId: String): Flow<List<Shop>>

    /** Retrieves all shops for a user (suspend). */
    suspend fun getShops(userId: String): List<Shop>

    /** Retrieves a single shop by ID (suspend). */
    suspend fun getShop(shopId: String): Shop?

    /** Retrieves a single shop by ID as a Flow. */
    fun getShopFlow(shopId: String): Flow<Shop?>

    /** Synchronizes shops from the remote data source. */
    suspend fun syncShops(userId: String): Result<List<Shop>>

    /** Creates a new shop. */
    suspend fun createShop(shop: Shop): Result<Shop>

    /** Updates an existing shop. */
    suspend fun updateShop(shop: Shop): Result<Shop>

    /** Deletes a shop. */
    suspend fun deleteShop(shopId: String): Result<Unit>
    
    // Category operations

    /** Retrieves categories for a shop as a Flow. */
    fun getCategoriesFlow(shopId: String): Flow<List<ProductCategory>>

    /** Creates a new product category. */
    suspend fun createCategory(category: ProductCategory): Result<ProductCategory>

    /** Updates a product category. */
    suspend fun updateCategory(category: ProductCategory): Result<ProductCategory>

    /** Deletes a product category. */
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    
    // Product operations

    /** Retrieves products for a shop as a Flow. */
    fun getProductsFlow(shopId: String): Flow<List<Product>>

    /** Creates a new product. */
    suspend fun createProduct(product: Product): Result<Product>

    /** Updates a product. */
    suspend fun updateProduct(product: Product): Result<Product>

    /** Deletes a product. */
    suspend fun deleteProduct(productId: String): Result<Unit>
}
