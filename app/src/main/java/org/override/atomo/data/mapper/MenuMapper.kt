package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.DishEntity
import org.override.atomo.data.local.entity.MenuCategoryEntity
import org.override.atomo.data.local.entity.MenuEntity
import org.override.atomo.data.remote.dto.DishDto
import org.override.atomo.data.remote.dto.MenuCategoryDto
import org.override.atomo.data.remote.dto.MenuDto
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory

// Menu mappers
fun MenuEntity.toDomain(): Menu = Menu(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    logoUrl = logoUrl,
    createdAt = createdAt
)

fun Menu.toEntity(): MenuEntity = MenuEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    logoUrl = logoUrl,
    createdAt = createdAt
)

fun MenuDto.toEntity(): MenuEntity = MenuEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    logoUrl = logoUrl,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

fun Menu.toDto(): MenuDto = MenuDto(
    id = id,
    userId = userId,
    name = name,
    description = description,
    isActive = isActive,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    logoUrl = logoUrl
)

// Category mappers
fun MenuCategoryEntity.toDomain(): MenuCategory = MenuCategory(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun MenuCategory.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun MenuCategoryDto.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

fun MenuCategory.toDto(): MenuCategoryDto = MenuCategoryDto(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder
)

// Dish mappers
fun DishEntity.toDomain(): Dish = Dish(
    id = id,
    menuId = menuId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isVisible = isVisible,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun Dish.toEntity(): DishEntity = DishEntity(
    id = id,
    menuId = menuId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isVisible = isVisible,
    sortOrder = sortOrder,
    createdAt = createdAt
)

fun DishDto.toEntity(): DishEntity = DishEntity(
    id = id,
    menuId = menuId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isVisible = isVisible,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

fun Dish.toDto(): DishDto = DishDto(
    id = id,
    menuId = menuId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isVisible = isVisible,
    sortOrder = sortOrder
)
