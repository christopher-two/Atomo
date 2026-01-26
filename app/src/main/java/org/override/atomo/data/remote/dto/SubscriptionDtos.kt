package org.override.atomo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PlanDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double = 0.0,
    val currency: String = "USD",
    val interval: String = "month",
    val features: JsonObject? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SubscriptionDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("plan_id") val planId: String,
    val status: String = "active",
    @SerialName("current_period_start") val currentPeriodStart: String? = null,
    @SerialName("current_period_end") val currentPeriodEnd: String? = null,
    @SerialName("cancel_at_period_end") val cancelAtPeriodEnd: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
