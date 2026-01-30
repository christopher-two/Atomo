/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * Room Entity for Menu.
 */
@Entity(
    tableName = "menus",
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
    @ColumnInfo(defaultValue = "1")
    val isSynced: Boolean = true,
    val createdAt: Long
)


/**
 * Room Entity for Menu Category.
 */
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
    @ColumnInfo(defaultValue = "1")
    val isSynced: Boolean = true,

    val createdAt: Long
)

/**
 * Room Entity for Dish.
 */
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
    @ColumnInfo(defaultValue = "1")
    val isSynced: Boolean = true,

    val createdAt: Long
)
