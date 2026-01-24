package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "portfolios",
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
data class PortfolioEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val isVisible: Boolean = true,
    val templateId: String = "minimalist",
    val primaryColor: String = "#000000",
    val fontFamily: String = "Inter",
    val createdAt: Long
)

@Entity(
    tableName = "portfolio_items",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEntity::class,
            parentColumns = ["id"],
            childColumns = ["portfolioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioId")]
)
data class PortfolioItemEntity(
    @PrimaryKey
    val id: String,
    val portfolioId: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val projectUrl: String?,
    val sortOrder: Int = 0,
    val createdAt: Long
)
