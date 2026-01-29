/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.ProductCategoryEntity
import org.override.atomo.data.local.entity.ProductEntity
import org.override.atomo.data.local.entity.ShopEntity
import org.override.atomo.data.remote.dto.ProductCategoryDto
import org.override.atomo.data.remote.dto.ProductDto
import org.override.atomo.data.remote.dto.ShopDto
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.ProductCategory
import org.override.atomo.domain.model.Shop

// Shop mappers

/** Maps ShopEntity to Shop domain model. */
fun ShopEntity.toDomain(): Shop = Shop(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps Shop domain model to ShopEntity. */
fun Shop.toEntity(): ShopEntity = ShopEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps ShopDto to ShopEntity. */
fun ShopDto.toEntity(): ShopEntity = ShopEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Shop domain model to ShopDto. */
fun Shop.toDto(): ShopDto = ShopDto(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    primaryColor = primaryColor,
    fontFamily = fontFamily
)

// Category mappers

/** Maps ProductCategoryEntity to ProductCategory domain model. */
fun ProductCategoryEntity.toDomain(): ProductCategory = ProductCategory(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps ProductCategory domain model to ProductCategoryEntity. */
fun ProductCategory.toEntity(): ProductCategoryEntity = ProductCategoryEntity(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps ProductCategoryDto to ProductCategoryEntity. */
fun ProductCategoryDto.toEntity(): ProductCategoryEntity = ProductCategoryEntity(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

// Product mappers

/** Maps ProductEntity to Product domain model. */
fun ProductEntity.toDomain(): Product = Product(
    id = id,
    shopId = shopId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isAvailable = isAvailable,
    stock = stock,
    createdAt = createdAt
)

/** Maps Product domain model to ProductEntity. */
fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    shopId = shopId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isAvailable = isAvailable,
    stock = stock,
    createdAt = createdAt
)

/** Maps ProductDto to ProductEntity. */
fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    shopId = shopId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isAvailable = isAvailable,
    stock = stock,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Product domain model to ProductDto. */
fun Product.toDto(): ProductDto = ProductDto(
    id = id,
    shopId = shopId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isAvailable = isAvailable,
    stock = stock
)
