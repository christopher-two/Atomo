package org.override.atomo.feature.menu.presentation

data class MenuState(
    val isLoading: Boolean = false,
    val menus: List<org.override.atomo.domain.model.Menu> = emptyList(),
    val error: String? = null
)