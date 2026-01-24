package org.override.atomo.feature.digital_menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

class DigitalMenuViewModel(
    private val menuUseCases: MenuUseCases,
    private val rootNavigation: RootNavigation,
    private val sessionRepository: SessionRepository
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
            is DigitalMenuAction.Back -> rootNavigation.back()
            
            // Dish Actions
            is DigitalMenuAction.CloseDishDialog -> _state.update { 
                it.copy(isDishDialogVisible = false, dishToEdit = null) 
            }
            is DigitalMenuAction.OpenAddDishDialog -> _state.update { 
                it.copy(isDishDialogVisible = true, dishToEdit = null) 
            }
            is DigitalMenuAction.OpenEditDishDialog -> _state.update { 
                it.copy(isDishDialogVisible = true, dishToEdit = action.dish) 
            }
            is DigitalMenuAction.SaveDish -> saveDish(action)
            is DigitalMenuAction.DeleteDish -> deleteDish(action.dish)
        }
    }

    private fun saveDish(action: DigitalMenuAction.SaveDish) {
        val currentDishes = _state.value.dishes.toMutableList()
        val editingDish = _state.value.dishToEdit

        if (editingDish != null) {
            // Edit existing dish
            val updatedDish = editingDish.copy(
                name = action.name,
                description = action.description,
                price = action.price,
                imageUrl = action.imageUrl
            )
            val index = currentDishes.indexOfFirst { it.id == editingDish.id }
            if (index != -1) {
                currentDishes[index] = updatedDish
            }
        } else {
            // Add new dish
            val newDish = Dish(
                id = UUID.randomUUID().toString(),
                menuId = "", // Pending until menu save or we can generate menu ID upfront
                categoryId = null,
                name = action.name,
                description = action.description,
                price = action.price,
                imageUrl = action.imageUrl,
                isVisible = true,
                sortOrder = currentDishes.size,
                createdAt = System.currentTimeMillis()
            )
            currentDishes.add(newDish)
        }

        _state.update { 
            it.copy(
                dishes = currentDishes,
                isDishDialogVisible = false,
                dishToEdit = null
            ) 
        }
    }

    private fun deleteDish(dish: Dish) {
        val currentDishes = _state.value.dishes.toMutableList()
        currentDishes.remove(dish)
        _state.update { it.copy(dishes = currentDishes) }
    }

    private fun saveMenu() {
        viewModelScope.launch {
            val uid = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch

            _state.update { it.copy(isLoading = true) }
            val current = _state.value
            
            val newMenuId = UUID.randomUUID().toString()
            
            val newMenu = Menu(
                id = newMenuId,
                userId = uid,
                name = current.menuName,
                description = current.menuDescription,
                isActive = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis(),
                dishes = emptyList() // We save dishes separately
            )
            
            // 1. Create Menu
            menuUseCases.createMenu(newMenu).onSuccess {
                // 2. Create Dishes
                // Note: In a real app we might want to do this transactionally or via a single endpoint
                // But for now we iterate since we have createDish
                var allSuccess = true
                current.dishes.forEach { dish ->
                    val dishWithMenuId = dish.copy(menuId = newMenuId)
                    val result = menuUseCases.createDish(dishWithMenuId)
                    if (result.isFailure) allSuccess = false
                }
                
                if (allSuccess) {
                    _state.update { it.copy(isLoading = false, isSaved = true) }
                    rootNavigation.back()
                } else {
                    _state.update { it.copy(isLoading = false, error = "Menu created but some dishes failed") }
                }
                
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}