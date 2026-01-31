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
import org.override.atomo.domain.model.MenuCategory

data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menus: List<Menu> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,

    // Editor
    val isEditing: Boolean = false,
    val editingMenu: Menu? = null,
    val hasUnsavedChanges: Boolean = false,

    // Overlay (Dialogs/Sheets)
    val activeOverlay: DigitalMenuOverlay? = null
)

sealed interface DigitalMenuOverlay {
    data class DishDialog(val dish: Dish? = null) : DigitalMenuOverlay
    data class CategoryDialog(val category: MenuCategory? = null) : DigitalMenuOverlay
    data object PreviewSheet : DigitalMenuOverlay
    data object DiscardConfirmation : DigitalMenuOverlay
    data object DeleteConfirmation : DigitalMenuOverlay
}