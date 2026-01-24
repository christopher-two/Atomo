package org.override.atomo.domain.model

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

data class MenuCategory(
    val id: String,
    val menuId: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: Long,
    val dishes: List<Dish> = emptyList()
)

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
