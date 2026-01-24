package org.override.atomo.feature.digital_menu.presentation

data class DigitalMenuState(
    val isLoading: Boolean = false,
    val menuName: String = "",
    val menuDescription: String = "",
    val error: String? = null,
    val isSaved: Boolean = false
)