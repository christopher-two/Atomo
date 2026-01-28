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
    val dishToEdit: Dish? = null
)