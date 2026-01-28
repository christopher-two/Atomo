/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

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

/** Maps MenuEntity to Menu domain model. */
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

/** Maps Menu domain model to MenuEntity. */
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

/** Maps MenuDto to MenuEntity. */
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

/** Maps Menu domain model to MenuDto. */
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

/** Maps MenuCategoryEntity to MenuCategory domain model. */
fun MenuCategoryEntity.toDomain(): MenuCategory = MenuCategory(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps MenuCategory domain model to MenuCategoryEntity. */
fun MenuCategory.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps MenuCategoryDto to MenuCategoryEntity. */
fun MenuCategoryDto.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps MenuCategory domain model to MenuCategoryDto. */
fun MenuCategory.toDto(): MenuCategoryDto = MenuCategoryDto(
    id = id,
    menuId = menuId,
    name = name,
    sortOrder = sortOrder
)

// Dish mappers

/** Maps DishEntity to Dish domain model. */
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

/** Maps Dish domain model to DishEntity. */
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

/** Maps DishDto to DishEntity. */
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

/** Maps Dish domain model to DishDto. */
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
