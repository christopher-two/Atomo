package org.override.atomo.domain.model

data class Portfolio(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val isVisible: Boolean,
    val templateId: String,
    val primaryColor: String,
    val fontFamily: String,
    val createdAt: Long,
    val items: List<PortfolioItem> = emptyList()
)

data class PortfolioItem(
    val id: String,
    val portfolioId: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val projectUrl: String?,
    val sortOrder: Int,
    val createdAt: Long
)
