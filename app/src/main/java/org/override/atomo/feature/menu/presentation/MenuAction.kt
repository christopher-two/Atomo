package org.override.atomo.feature.menu.presentation

sealed interface MenuAction {
    data object CreateMenu : MenuAction
    data class DeleteMenu(val id: String) : MenuAction
    data class OpenMenu(val id: String) : MenuAction
}