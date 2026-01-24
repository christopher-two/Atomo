package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "menus",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class MenuEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val isActive: Boolean = true,
    val templateId: String = "minimalist",
    val primaryColor: String = "#000000",
    val fontFamily: String = "Inter",
    val logoUrl: String?,
    val createdAt: Long
)

@Entity(
    tableName = "menu_categories",
    foreignKeys = [
        ForeignKey(
            entity = MenuEntity::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("menuId")]
)
data class MenuCategoryEntity(
    @PrimaryKey
    val id: String,
    val menuId: String,
    val name: String,
    val sortOrder: Int = 0,
    val createdAt: Long
)

@Entity(
    tableName = "dishes",
    foreignKeys = [
        ForeignKey(
            entity = MenuEntity::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("menuId"), Index("categoryId")]
)
data class DishEntity(
    @PrimaryKey
    val id: String,
    val menuId: String,
    val categoryId: String?,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isVisible: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long
)
