package org.override.atomo.domain.model

data class Plan(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val currency: String,
    val interval: String,
    val features: List<String>,
    val isActive: Boolean,
    val createdAt: Long
)

data class Subscription(
    val id: String,
    val userId: String,
    val planId: String,
    val status: SubscriptionStatus,
    val currentPeriodStart: Long,
    val currentPeriodEnd: Long?,
    val cancelAtPeriodEnd: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

enum class SubscriptionStatus {
    ACTIVE,
    CANCELED,
    PAST_DUE,
    TRIALING
}
