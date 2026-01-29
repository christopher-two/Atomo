/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Subscription Plan.
 */
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

/**
 * Room Entity for User Subscription.
 */
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
