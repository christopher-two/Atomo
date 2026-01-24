package org.override.atomo.feature.digital_menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.usecase.menu.MenuUseCases
import java.util.UUID

class DigitalMenuViewModel(
    private val menuUseCases: MenuUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(DigitalMenuState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DigitalMenuState()
        )

    fun onAction(action: DigitalMenuAction) {
        when (action) {
            is DigitalMenuAction.UpdateName -> _state.update { it.copy(menuName = action.name) }
            is DigitalMenuAction.UpdateDescription -> _state.update { it.copy(menuDescription = action.description) }
            is DigitalMenuAction.SaveMenu -> saveMenu()
        }
    }

    private fun saveMenu() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val current = _state.value
            val newMenu = Menu(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id", // TODO
                name = current.menuName,
                description = current.menuDescription,
                isActive = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis()
            )
            
            menuUseCases.createMenu(newMenu).onSuccess {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}