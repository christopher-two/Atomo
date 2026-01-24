package org.override.atomo.feature.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.usecase.menu.MenuUseCases
import java.util.UUID

class MenuViewModel(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(MenuState())
    val state = _state
        .onStart { loadMenus() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MenuState(),
        )

    fun onAction(action: MenuAction) {
        when (action) {
            is MenuAction.CreateMenu -> createMenu()
            is MenuAction.DeleteMenu -> deleteMenu(action.id)
            is MenuAction.OpenMenu -> { /* Handle navigation */ }
        }
    }

    private fun loadMenus() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO: Real user
            menuUseCases.getMenus(userId).collect { menus ->
                _state.update { it.copy(isLoading = false, menus = menus) }
            }
        }
    }
    
    // For now create directly here for testing, or navigate to DigitalMenu feature
    private fun createMenu() {
         viewModelScope.launch {
            val newMenu = Menu(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id",
                name = "New Menu",
                description = "",
                isActive = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis()
            )
            menuUseCases.createMenu(newMenu)
        }
    }

    private fun deleteMenu(id: String) {
        viewModelScope.launch {
            menuUseCases.deleteMenu(id)
        }
    }
}