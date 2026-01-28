package org.override.atomo.feature.digital_menu.presentation

import org.override.atomo.domain.model.Dish

data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menus: List<org.override.atomo.domain.model.Menu> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,

    // Editor State
    val isEditing: Boolean = false,
    val editingMenu: org.override.atomo.domain.model.Menu? = null,
    val showPreviewSheet: Boolean = false,
    
    // Dish Editor State (kept for dialogs)
    val isDishDialogVisible: Boolean = false,
    val dishToEdit: Dish? = null
)