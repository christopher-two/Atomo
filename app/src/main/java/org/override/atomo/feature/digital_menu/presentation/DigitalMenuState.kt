package org.override.atomo.feature.digital_menu.presentation

import org.override.atomo.domain.model.Dish

data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menuName: String = "",
    val menuDescription: String = "",
    val dishes: List<Dish> = emptyList(),
    val error: String? = null,
    val isSaved: Boolean = false,
    val isDishDialogVisible: Boolean = false,
    val dishToEdit: Dish? = null,
    val existingMenuId: String? = null,
    val limitReached: Boolean = false
)