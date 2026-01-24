package org.override.atomo.domain.model

data class Shop(
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val primaryColor: String,
    val fontFamily: String,
    val createdAt: Long,
    val categories: List<ProductCategory> = emptyList(),
    val products: List<Product> = emptyList()
)

data class ProductCategory(
    val id: String,
    val shopId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: Long,
    val products: List<Product> = emptyList()
)

data class Product(
    val id: String,
    val shopId: String,
    val categoryId: String?,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val stock: Int,
    val createdAt: Long
)
