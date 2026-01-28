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
 * Represents a digital menu for a restaurant or food service.
 *
 * @property id Unique identifier for the menu.
 * @property userId The ID of the user who owns this menu.
 * @property name The display name of the menu.
 * @property description Optional description of the menu.
 * @property isActive Whether the menu is currently active and visible to customers.
 * @property templateId The ID of the UI template.
 * @property primaryColor The primary branding color.
 * @property fontFamily The selected font family.
 * @property logoUrl Optional URL for the menu logo.
 * @property createdAt Timestamp of creation.
 * @property categories List of categories in this menu.
 * @property dishes List of dishes in this menu.
 */
data class Menu(
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val templateId: String,
    val primaryColor: String,
    val fontFamily: String,
    val logoUrl: String?,
    val createdAt: Long,
    val categories: List<MenuCategory> = emptyList(),
    val dishes: List<Dish> = emptyList()
)

/**
 * Represents a category within a menu (e.g., "Starters", "Mains").
 *
 * @property id Unique identifier.
 * @property menuId The menu this category belongs to.
 * @property name The category name.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 * @property dishes List of dishes in this category.
 */
data class MenuCategory(
    val id: String,
    val menuId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: Long,
    val dishes: List<Dish> = emptyList()
)

/**
 * Represents a food or drink item.
 *
 * @property id Unique identifier.
 * @property menuId The menu this dish belongs to.
 * @property categoryId The ID of the category this dish belongs to.
 * @property name The name of the dish.
 * @property description Optional detailed description.
 * @property price The price of the dish.
 * @property imageUrl Optional image URL.
 * @property isVisible Whether the dish is visible to customers.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 */
data class Dish(
    val id: String,
    val menuId: String,
    val categoryId: String?,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isVisible: Boolean,
    val sortOrder: Int,
    val createdAt: Long
)
