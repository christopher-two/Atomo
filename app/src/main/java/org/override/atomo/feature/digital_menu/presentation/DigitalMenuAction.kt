/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.model.MenuCategory

sealed interface DigitalMenuAction {
    data object CreateMenu : DigitalMenuAction
    data class DeleteMenu(val id: String) : DigitalMenuAction
    data class OpenMenu(val id: String) : DigitalMenuAction
    data object UpgradePlan : DigitalMenuAction
    data object Back : DigitalMenuAction

    // Menu Edition Action
    data object ToggleEditMode : DigitalMenuAction
    data class UpdateEditingMenu(val menu: Menu) : DigitalMenuAction
    data class UpdateTemplate(val templateId: String) : DigitalMenuAction
    data object SaveMenu : DigitalMenuAction
    data object CancelEdit : DigitalMenuAction

    /** Abre o cierra cualquier overlay (dialog/sheet). Pasar null para cerrar. */
    data class SetOverlay(val overlay: DigitalMenuOverlay?) : DigitalMenuAction

    data object ConfirmDelete : DigitalMenuAction
    data object ConfirmDiscard : DigitalMenuAction

    // Dish Actions
    data object OpenAddDishDialog : DigitalMenuAction
    data class OpenEditDishDialog(val dish: Dish) : DigitalMenuAction
    data object CloseDishDialog : DigitalMenuAction
    data class SaveDish(
        val name: String,
        val description: String,
        val price: Double,
        val imageUrl: String?,
        val categoryId: String?
    ) : DigitalMenuAction
    data class DeleteDish(val dish: Dish) : DigitalMenuAction

    // Category Actions
    data object OpenAddCategoryDialog : DigitalMenuAction
    data class OpenEditCategoryDialog(val category: MenuCategory) : DigitalMenuAction
    data object CloseCategoryDialog : DigitalMenuAction
    data class SaveCategory(val name: String) : DigitalMenuAction
    data class DeleteCategory(val category: MenuCategory) : DigitalMenuAction
}