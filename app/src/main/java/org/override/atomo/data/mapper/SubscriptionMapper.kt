package org.override.atomo.data.mapper

import kotlinx.serialization.json.Json
import org.override.atomo.data.local.entity.PlanEntity
import org.override.atomo.data.local.entity.SubscriptionEntity
import org.override.atomo.data.remote.dto.PlanDto
import org.override.atomo.data.remote.dto.SubscriptionDto
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.model.SubscriptionStatus

private val json = Json { ignoreUnknownKeys = true }

fun PlanEntity.toDomain(): Plan = Plan(
    id = id,
    name = name,
    description = description,
    price = price,
    currency = currency,
    interval = interval,
    features = features?.let { 
        runCatching { json.decodeFromString<List<String>>(it) }.getOrDefault(emptyList())
    } ?: emptyList(),
    isActive = isActive,
    createdAt = createdAt
)

fun Plan.toEntity(): PlanEntity = PlanEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    currency = currency,
    interval = interval,
    features = json.encodeToString(kotlinx.serialization.serializer(), features),
    isActive = isActive,
    createdAt = createdAt
)

fun PlanDto.toEntity(): PlanEntity = PlanEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    currency = currency,
    interval = interval,
    features = features?.let { jsonObject ->
        val featureList = jsonObject.entries.map { (key, value) ->
            formatFeature(key, value.toString().replace("\"", ""))
        }
        json.encodeToString(featureList)
    },
    isActive = isActive,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

private fun formatFeature(key: String, value: String): String {
    return try {
        val readableKey = when (key) {
            "max_menus" -> "Menús"
            "max_cvs" -> "CVs"
            "max_portfolios" -> "Portafolios"
            "max_shops" -> "Tiendas"
            "max_invitations" -> "Invitaciones"
            "custom_domain" -> "Dominio Personalizado"
            "remove_branding" -> "Sin Marca de Agua"
            "premium_templates" -> "Plantillas Premium"
            "analytics" -> "Analíticas Avanzadas"
            "priority_support" -> "Soporte Prioritario"
            else -> key.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
        
        when {
            value == "true" -> readableKey
            value == "false" -> "Sin $readableKey"
            value == "-1" || value == "unlimited" -> "$readableKey Ilimitados"
            key.startsWith("max_") -> "$value $readableKey"
            else -> "$readableKey: $value"
        }
    } catch (e: Exception) {
        "$key: $value"
    }
}

fun SubscriptionEntity.toDomain(): Subscription = Subscription(
    id = id,
    userId = userId,
    planId = planId,
    status = SubscriptionStatus.entries.find { it.name.equals(status, ignoreCase = true) } ?: SubscriptionStatus.ACTIVE,
    currentPeriodStart = currentPeriodStart,
    currentPeriodEnd = currentPeriodEnd,
    cancelAtPeriodEnd = cancelAtPeriodEnd,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Subscription.toEntity(): SubscriptionEntity = SubscriptionEntity(
    id = id,
    userId = userId,
    planId = planId,
    status = status.name.lowercase(),
    currentPeriodStart = currentPeriodStart,
    currentPeriodEnd = currentPeriodEnd,
    cancelAtPeriodEnd = cancelAtPeriodEnd,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SubscriptionDto.toEntity(): SubscriptionEntity = SubscriptionEntity(
    id = id,
    userId = userId,
    planId = planId,
    status = status,
    currentPeriodStart = currentPeriodStart?.let { parseTimestamp(it) } ?: System.currentTimeMillis(),
    currentPeriodEnd = currentPeriodEnd?.let { parseTimestamp(it) },
    cancelAtPeriodEnd = cancelAtPeriodEnd,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis(),
    updatedAt = updatedAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)