package org.override.atomo.feature.digital_menu.presentation

sealed interface DigitalMenuAction {
    data class UpdateName(val name: String) : DigitalMenuAction
    data class UpdateDescription(val description: String) : DigitalMenuAction
    data object SaveMenu : DigitalMenuAction
}