/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Menu.
 */
@Serializable
data class MenuDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("template_id") val templateId: String = "minimalist",
    @SerialName("primary_color") val primaryColor: String = "#000000",
    @SerialName("font_family") val fontFamily: String = "Inter",
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for Menu Category.
 */
@Serializable
data class MenuCategoryDto(
    val id: String,
    @SerialName("menu_id") val menuId: String,
    val name: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

/**
 * Data Transfer Object for Dish.
 */
@Serializable
data class DishDto(
    val id: String,
    @SerialName("menu_id") val menuId: String,
    @SerialName("category_id") val categoryId: String? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
