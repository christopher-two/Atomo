/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.model

/**
 * Represents an e-commerce shop.
 *
 * @property id Unique identifier for the shop.
 * @property userId The ID of the owner.
 * @property name The name of the shop.
 * @property description Optional description.
 * @property isActive Whether the shop is active and visible.
 * @property primaryColor The primary color hex code.
 * @property fontFamily The font family name.
 * @property createdAt Timestamp of creation.
 * @property categories List of product categories.
 * @property products List of products in the shop.
 */
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

/**
 * Represents a product category.
 *
 * @property id Unique identifier.
 * @property shopId The shop this category belongs to.
 * @property name The category name.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 * @property products List of products in this category.
 */
data class ProductCategory(
    val id: String,
    val shopId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: Long,
    val products: List<Product> = emptyList()
)

/**
 * Represents an item for sale in the shop.
 *
 * @property id Unique identifier.
 * @property shopId The shop this product belongs to.
 * @property categoryId The ID of the category.
 * @property name The product name.
 * @property description Optional description.
 * @property price The product price.
 * @property imageUrl Optional image URL.
 * @property isAvailable Whether the product is in stock/available.
 * @property stock Current stock quantity (optional logic).
 * @property createdAt Creation timestamp.
 */
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
