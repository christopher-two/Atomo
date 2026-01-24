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
fun ProductCategoryEntity.toDomain(): ProductCategory = ProductCategory(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun ProductCategory.toEntity(): ProductCategoryEntity = ProductCategoryEntity(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun ProductCategoryDto.toEntity(): ProductCategoryEntity = ProductCategoryEntity(
    id = id,
    shopId = shopId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

// Product mappers
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
