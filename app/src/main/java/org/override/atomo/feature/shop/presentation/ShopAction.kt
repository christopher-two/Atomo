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
 * Represents the intent/actions that can be performed on the Shop screen.
 */
sealed interface ShopAction {
    /** Create a new Shop. */
    data object CreateShop : ShopAction
    
    /** Delete a Shop by its ID. */
    data class DeleteShop(val id: String) : ShopAction
    
    /** Open a Shop for viewing/editing. */
    data class OpenShop(val id: String) : ShopAction
    
    /** Navigate to upgrade plan screen. */
    data object UpgradePlan : ShopAction
    
    // Editor Actions
    /** Toggle between view and edit modes. */
    data object ToggleEditMode : ShopAction
    
    /** Update the Shop currently being edited in the state buffer. */
    data class UpdateEditingShop(val shop: Shop) : ShopAction
    
    /** Save changes to the current Shop. */
    data object SaveShop : ShopAction
    
    /** Cancel current edits and revert to original state. */
    data object CancelEdit : ShopAction
    
    /** Show or hide the preview bottom sheet. */
    data class TogglePreviewSheet(val show: Boolean) : ShopAction
    
    /** Navigate back or close current view. */
    data object Back : ShopAction
}