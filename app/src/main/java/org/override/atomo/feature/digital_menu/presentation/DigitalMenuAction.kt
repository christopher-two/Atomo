package org.override.atomo.feature.digital_menu.presentation

import org.override.atomo.domain.model.Dish

sealed interface DigitalMenuAction {
    data class UpdateName(val name: String) : DigitalMenuAction
    data class UpdateDescription(val description: String) : DigitalMenuAction
    data object SaveMenu : DigitalMenuAction
    data object Back : DigitalMenuAction

    // Dish Actions
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
    data object UpgradePlan : DigitalMenuAction
}