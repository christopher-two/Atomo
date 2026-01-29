/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.shop

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.repository.ShopRepository

/**
 * Wrapper for all Shop-related use cases.
 *
 * @property getShops Retrieves all Shops for a user.
 * @property getShop Retrieves a single Shop by ID.
 * @property syncShops Synchronizes Shops from the backend.
 * @property createShop Creates a new Shop.
 * @property updateShop Updates an existing Shop.
 * @property deleteShop Deletes a Shop.
 * @property createCategory Creates a product category.
 * @property createProduct Creates a product.
 * @property updateProduct Updates a product.
 * @property deleteProduct Deletes a product.
 */
data class ShopUseCases(
    val getShops: GetShopsUseCase,
    val getShop: GetShopUseCase,
    val syncShops: SyncShopsUseCase,
    val createShop: CreateShopUseCase,
    val updateShop: UpdateShopUseCase,
    val deleteShop: DeleteShopUseCase,
    val createCategory: CreateProductCategoryUseCase,
    val createProduct: CreateProductUseCase,
    val updateProduct: UpdateProductUseCase,
    val deleteProduct: DeleteProductUseCase
)

/** Retrieves all shops for a user as a Flow. */
class GetShopsUseCase(private val repository: ShopRepository) {
    operator fun invoke(userId: String): Flow<List<Shop>> = repository.getShopsFlow(userId)
}

/** Retrieves a single shop by ID as a Flow. */
class GetShopUseCase(private val repository: ShopRepository) {
    operator fun invoke(shopId: String): Flow<Shop?> = repository.getShopFlow(shopId)
}

/** Synchronizes shops from the server. */
class SyncShopsUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(userId: String): Result<List<Shop>> = repository.syncShops(userId)
}

/** Creates a new shop. */
class CreateShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shop: Shop): Result<Shop> = repository.createShop(shop)
}

/** Updates an existing shop. */
class UpdateShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shop: Shop): Result<Shop> = repository.updateShop(shop)
}

/** Deletes a shop by ID. */
class DeleteShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shopId: String): Result<Unit> = repository.deleteShop(shopId)
}

/** Creates a product category within a shop. */
class CreateProductCategoryUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(category: ProductCategory): Result<ProductCategory> = repository.createCategory(category)
}

/** Creates a new product in a shop. */
class CreateProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(product: Product): Result<Product> = repository.createProduct(product)
}

/** Updates an existing product. */
class UpdateProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(product: Product): Result<Product> = repository.updateProduct(product)
}

/** Deletes a product by ID. */
class DeleteProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(productId: String): Result<Unit> = repository.deleteProduct(productId)
}
