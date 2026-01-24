package org.override.atomo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PortfolioDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    val description: String? = null,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("template_id") val templateId: String = "minimalist",
    @SerialName("primary_color") val primaryColor: String = "#000000",
    @SerialName("font_family") val fontFamily: String = "Inter",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PortfolioItemDto(
    val id: String,
    @SerialName("portfolio_id") val portfolioId: String,
    val title: String,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("project_url") val projectUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
