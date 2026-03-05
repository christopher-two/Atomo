/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import androidx.compose.runtime.Stable
import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.model.MenuCategory

@Stable
data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menus: List<Menu> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    val isEditing: Boolean = false,
    val editingMenu: Menu? = null,
    /** Snapshot del menú al iniciar la edición. Usado para detectar cambios sin guardar. */
    val menuSnapshot: Menu? = null,
    val activeOverlay: DigitalMenuOverlay? = null
) {
    /** True si los campos de presentación difieren del snapshot capturado al abrir el editor. */
    val hasUnsavedChanges: Boolean
        get() = isEditing && editingMenu != null && menuSnapshot != null &&
            (editingMenu.name         != menuSnapshot.name         ||
             editingMenu.description  != menuSnapshot.description  ||
             editingMenu.primaryColor != menuSnapshot.primaryColor ||
             editingMenu.fontFamily   != menuSnapshot.fontFamily)
}

/**
 * Fusiona el estado local con los menús provenientes del Flow reactivo.
 * Conserva los cambios del usuario en edición; actualiza solo categories/dishes
 * desde la fuente de verdad.
 */
fun DigitalMenuState.withLiveMenus(menus: List<Menu>): DigitalMenuState {
    val activeMenu = if (isEditing && editingMenu != null) {
        val liveMenu = menus.find { it.id == editingMenu.id } ?: editingMenu
        editingMenu.copy(categories = liveMenu.categories, dishes = liveMenu.dishes)
    } else {
        menus.firstOrNull()
    }
    return copy(menus = menus, editingMenu = activeMenu)
}

sealed interface DigitalMenuOverlay {
    data class DishDialog(val dish: Dish? = null) : DigitalMenuOverlay
    data class CategoryDialog(val category: MenuCategory? = null) : DigitalMenuOverlay
    data object PreviewSheet : DigitalMenuOverlay
    data object DiscardConfirmation : DigitalMenuOverlay
    data object DeleteConfirmation : DigitalMenuOverlay
}