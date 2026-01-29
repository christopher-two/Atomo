/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu

sealed interface DigitalMenuAction {
    data object CreateMenu : DigitalMenuAction
    data class DeleteMenu(val id: String) : DigitalMenuAction
    data class OpenMenu(val id: String) : DigitalMenuAction
    data object UpgradePlan : DigitalMenuAction

    // Editor Actions
    data object ToggleEditMode : DigitalMenuAction
    data class UpdateEditingMenu(val menu: Menu) : DigitalMenuAction
    data object SaveMenu : DigitalMenuAction
    data object CancelEdit : DigitalMenuAction
    data class TogglePreviewSheet(val show: Boolean) : DigitalMenuAction
    data object Back : DigitalMenuAction

    // Dish Actions (Sub-actions for editing menu)
    data object OpenAddDishDialog : DigitalMenuAction
    data class OpenEditDishDialog(val dish: Dish) : DigitalMenuAction
    data object CloseDishDialog : DigitalMenuAction
    data class SaveDish(
        val name: String,
        val description: String,
        val price: Double,
        val imageUrl: String?
    ) : DigitalMenuAction
    data class DeleteDish(val dish: Dish) : DigitalMenuAction

    // Delete Confirmation
    data object ShowDeleteConfirmation : DigitalMenuAction
    data object HideDeleteConfirmation : DigitalMenuAction
    data object ConfirmDelete : DigitalMenuAction
}