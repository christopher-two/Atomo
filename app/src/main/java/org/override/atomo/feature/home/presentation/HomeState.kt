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
    val availableServiceTypes: List<ServiceType> = emptyList(), // Calculated in ViewModel
    val showUpgradeDialog: Boolean = false,
    val upgradeDialogMessage: String = "",
    val isRefreshing: Boolean = false,
    val isMenuSheetOpen: Boolean = false
) {
    
    val totalServiceCount: Int
        get() = existingServices.count { it.value }
}