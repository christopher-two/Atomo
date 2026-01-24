package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val price: Double = 0.0,
    val currency: String = "USD",
    val interval: String = "month",
    val features: String?, // JSON string
    val isActive: Boolean = true,
    val createdAt: Long
)

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId", unique = true), Index("planId")]
)
data class SubscriptionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val planId: String,
    val status: String = "active",
    val currentPeriodStart: Long,
    val currentPeriodEnd: Long?,
    val cancelAtPeriodEnd: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
