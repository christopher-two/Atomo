package org.override.atomo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("primary_color") val primaryColor: String = "#000000",
    @SerialName("font_family") val fontFamily: String = "Inter",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ProductCategoryDto(
    val id: String,
    @SerialName("shop_id") val shopId: String,
    val name: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ProductDto(
    val id: String,
    @SerialName("shop_id") val shopId: String,
    @SerialName("category_id") val categoryId: String? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_available") val isAvailable: Boolean = true,
    val stock: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
