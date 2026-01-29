/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.shop.presentation

import org.override.atomo.domain.model.Shop

/**
 * Represents the UI state for the Shop feature.
 *
 * @property isLoading Whether a background operation is in progress.
 * @property shops The list of user's Shops.
 * @property error Error message if any operation failed.
 * @property canCreate Whether the user is allowed to create more Shops (plan limit).
 * @property limitReached Whether the plan limit for Shops has been reached.
 * @property isEditing Whether the UI is currently in edit mode.
 * @property editingShop The temporary Shop object holding unsaved changes during editing.
 * @property showPreviewSheet Whether the preview bottom sheet is visible.
 */
data class ShopState(
    val isLoading: Boolean = false,
    val shops: List<Shop> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingShop: Shop? = null,
    val hasChanges: Boolean = false,
    val showPreviewSheet: Boolean = false
)