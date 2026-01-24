package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shops",
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
data class ShopEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String?,
    val isActive: Boolean = true,
    val primaryColor: String = "#000000",
    val fontFamily: String = "Inter",
    val createdAt: Long
)

@Entity(
    tableName = "product_categories",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("shopId")]
)
data class ProductCategoryEntity(
    @PrimaryKey
    val id: String,
    val shopId: String,
    val name: String,
    val sortOrder: Int = 0,
    val createdAt: Long
)

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("shopId"), Index("categoryId")]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val shopId: String,
    val categoryId: String?,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean = true,
    val stock: Int = 0,
    val createdAt: Long
)
