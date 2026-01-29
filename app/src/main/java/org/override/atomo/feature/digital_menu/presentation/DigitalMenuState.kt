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

data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menus: List<Menu> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,

    // Editor State
    val isEditing: Boolean = false,
    val editingMenu: Menu? = null,
    val showPreviewSheet: Boolean = false,
    
    // Dish Editor State (kept for dialogs)
    val isDishDialogVisible: Boolean = false,
    val dishToEdit: Dish? = null,

    // Category Editor State
    val isCategoryDialogVisible: Boolean = false,
    val categoryToEdit: org.override.atomo.domain.model.MenuCategory? = null,
    
    // Delete Confirmation
    val isDeleteDialogVisible: Boolean = false
)