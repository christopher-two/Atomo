package org.override.atomo.domain.usecase.shop

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.repository.ShopRepository

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

class GetShopsUseCase(private val repository: ShopRepository) {
    operator fun invoke(userId: String): Flow<List<Shop>> = repository.getShopsFlow(userId)
}

class GetShopUseCase(private val repository: ShopRepository) {
    operator fun invoke(shopId: String): Flow<Shop?> = repository.getShopFlow(shopId)
}

class SyncShopsUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(userId: String): Result<List<Shop>> = repository.syncShops(userId)
}

class CreateShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shop: Shop): Result<Shop> = repository.createShop(shop)
}

class UpdateShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shop: Shop): Result<Shop> = repository.updateShop(shop)
}

class DeleteShopUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(shopId: String): Result<Unit> = repository.deleteShop(shopId)
}

class CreateProductCategoryUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(category: ProductCategory): Result<ProductCategory> = repository.createCategory(category)
}

class CreateProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(product: Product): Result<Product> = repository.createProduct(product)
}

class UpdateProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(product: Product): Result<Product> = repository.updateProduct(product)
}

class DeleteProductUseCase(private val repository: ShopRepository) {
    suspend operator fun invoke(productId: String): Result<Unit> = repository.deleteProduct(productId)
}
