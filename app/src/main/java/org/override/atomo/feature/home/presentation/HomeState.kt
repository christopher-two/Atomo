/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.home.presentation

import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.model.Subscription
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.navigation.AppTab

data class HomeState(
    val isFabExpanded: Boolean = false,
    val currentTab: AppTab = AppTab.DASHBOARD,
    val currentSubscription: Subscription? = null,
    val currentPlan: Plan? = null,
    val existingServices: Map<ServiceType, Boolean> = emptyMap(),
    val showUpgradeDialog: Boolean = false,
    val upgradeDialogMessage: String = "",
    val isRefreshing: Boolean = false,
    val isMenuSheetOpen: Boolean = false
) {
    /**
     * Returns the list of service types that can be created based on:
     * 1. Service type doesn't already exist (1 per type limit)
     * 2. Total service count is within plan limit
     */
    val availableServiceTypes: List<ServiceType>
        get() {
            val planName = currentPlan?.name?.lowercase() ?: "free"
            val maxTotal = when {
                planName.contains("quantum") -> Int.MAX_VALUE // Unlimited
                planName.contains("core") -> 2
                else -> 1 // Free plan
            }
            
            val currentCount = existingServices.count { it.value }
            val canAddMore = currentCount < maxTotal
            
            return if (canAddMore) {
                ServiceType.entries.filter { type ->
                    existingServices[type] != true // Only show services that don't exist
                }
            } else {
                emptyList()
            }
        }
    
    val totalServiceCount: Int
        get() = existingServices.count { it.value }
}