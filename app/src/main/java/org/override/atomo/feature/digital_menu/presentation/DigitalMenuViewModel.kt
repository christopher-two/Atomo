/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.digital_menu.domain.model.Dish
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.model.MenuCategory
import org.override.atomo.feature.digital_menu.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.profile.domain.usecase.profile.ProfileUseCases
import org.override.atomo.feature.session.domain.repository.SessionRepository
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanCreateResult
import org.override.atomo.feature.subscription.domain.usecase.subscription.CanCreateServiceUseCase
import java.util.UUID

class DigitalMenuViewModel(
    private val sessionRepository: SessionRepository,
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private val _state = MutableStateFlow(DigitalMenuState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val menusFlow = sessionRepository.getCurrentUserId()
        .filterNotNull()
        .flatMapLatest { userId ->
            checkCreationLimit(userId)
            menuUseCases.getMenus(userId)
        }
        
    private val templatesFlow = menuUseCases.getMenuTemplates()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val usernameFlow = sessionRepository.getCurrentUserId()
        .filterNotNull()
        .flatMapLatest { userId ->
            profileUseCases.getProfile(userId).map { it?.username }
        }

    val state = combine(menusFlow, templatesFlow, usernameFlow, _state) { menus, templates, username, local ->
        local.withLiveMenusAndTemplates(menus, templates, username)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DigitalMenuState(isLoading = true)
    )

    private val _events = Channel<DigitalMenuEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: DigitalMenuAction) {
        when (action) {
            is DigitalMenuAction.CreateMenu -> createMenu()
            is DigitalMenuAction.DeleteMenu -> deleteMenu(action.id)
            is DigitalMenuAction.OpenMenu -> openMenu(action.id)
            is DigitalMenuAction.UpgradePlan -> Unit

            is DigitalMenuAction.SetOverlay -> updateLocal { it.copy(activeOverlay = action.overlay) }

            DigitalMenuAction.Back -> handleBack()

            DigitalMenuAction.ToggleEditMode -> toggleEditMode()
            is DigitalMenuAction.UpdateEditingMenu -> updateLocal { it.copy(editingMenu = action.menu) }
            is DigitalMenuAction.UpdateTemplate -> {
                val menu = state.value.editingMenu
                if (menu != null) {
                    updateLocal { 
                        it.copy(
                            editingMenu = menu.copy(templateId = action.templateId),
                            activeOverlay = null 
                        ) 
                    }
                }
            }
            DigitalMenuAction.SaveMenu -> saveMenu()
            DigitalMenuAction.CancelEdit -> handleCancelEdit()

            DigitalMenuAction.ConfirmDelete -> confirmDeleteMenu()
            DigitalMenuAction.ConfirmDiscard -> confirmDiscard()

            is DigitalMenuAction.OpenAddDishDialog,
            is DigitalMenuAction.OpenEditDishDialog,
            DigitalMenuAction.CloseDishDialog,
            is DigitalMenuAction.SaveDish,
            is DigitalMenuAction.DeleteDish -> handleDishAction(action)

            is DigitalMenuAction.OpenAddCategoryDialog,
            is DigitalMenuAction.OpenEditCategoryDialog,
            DigitalMenuAction.CloseCategoryDialog,
            is DigitalMenuAction.SaveCategory,
            is DigitalMenuAction.DeleteCategory -> handleCategoryAction(action)
        }
    }

    private fun handleDishAction(action: DigitalMenuAction) {
        when (action) {
            DigitalMenuAction.OpenAddDishDialog -> updateLocal {
                it.copy(activeOverlay = DigitalMenuOverlay.DishDialog(null))
            }

            is DigitalMenuAction.OpenEditDishDialog -> updateLocal {
                it.copy(activeOverlay = DigitalMenuOverlay.DishDialog(action.dish))
            }

            DigitalMenuAction.CloseDishDialog -> updateLocal { it.copy(activeOverlay = null) }
            is DigitalMenuAction.SaveDish -> saveDish(action)
            is DigitalMenuAction.DeleteDish -> deleteDish(action.dish)
            else -> Unit
        }
    }

    private fun handleCategoryAction(action: DigitalMenuAction) {
        when (action) {
            DigitalMenuAction.OpenAddCategoryDialog -> updateLocal {
                it.copy(activeOverlay = DigitalMenuOverlay.CategoryDialog(null))
            }

            is DigitalMenuAction.OpenEditCategoryDialog -> updateLocal {
                it.copy(activeOverlay = DigitalMenuOverlay.CategoryDialog(action.category))
            }

            DigitalMenuAction.CloseCategoryDialog -> updateLocal { it.copy(activeOverlay = null) }
            is DigitalMenuAction.SaveCategory -> saveCategory(action.name)
            is DigitalMenuAction.DeleteCategory -> deleteCategory(action.category)
            else -> Unit
        }
    }

    private fun createMenu() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            updateLocal { it.copy(isLoading = true) }

            if (
                canCreateServiceUseCase(
                    userId,
                    ServiceType.DIGITAL_MENU
                ) !is CanCreateResult.Success
            ) {
                updateLocal { it.copy(isLoading = false) }
                return@launch
            }

            val newMenu = Menu(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "My New Menu",
                description = "Digital Menu Description",
                isActive = true,
                templateId = state.value.templates.firstOrNull()?.id ?: "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis()
            )

            menuUseCases.createMenu(newMenu)
                .onSuccess {
                    updateLocal {
                        it.copy(
                            isLoading = false,
                            isEditing = true,
                            editingMenu = newMenu,
                            menuSnapshot = newMenu
                        )
                    }
                }
                .onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun saveMenu() {
        val menu = state.value.editingMenu
        if (menu == null) {
            updateLocal { it.copy(error = "No hay un menú en edición para guardar") }
            return
        }
        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            menuUseCases.updateMenu(menu)
                .onSuccess {
                    updateLocal {
                        it.copy(isLoading = false, isEditing = false, menuSnapshot = null)
                    }
                    sendEvent(DigitalMenuEvent.MenuSaved)
                }
                .onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun deleteMenu(id: String) {
        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            menuUseCases.deleteMenu(id)
                .onSuccess {
                    updateLocal {
                        it.copy(
                            isLoading = false,
                            isEditing = false,
                            editingMenu = null,
                            menuSnapshot = null
                        )
                    }
                }
                .onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                    snackbarManager.showMessage(error.message ?: "Error deleting menu")
                }
        }
    }

    private fun toggleEditMode() {
        if (!_state.value.isEditing) {
            val active = state.value.editingMenu
            updateLocal { it.copy(isEditing = true, editingMenu = active, menuSnapshot = active) }
        } else {
            handleCancelEdit()
        }
    }

    private fun handleCancelEdit() {
        if (state.value.hasUnsavedChanges) {
            updateLocal { it.copy(activeOverlay = DigitalMenuOverlay.DiscardConfirmation) }
        } else {
            confirmDiscard()
        }
    }

    private fun confirmDiscard() {
        updateLocal {
            it.copy(
                isEditing = false,
                editingMenu = null,
                menuSnapshot = null,
                activeOverlay = null
            )
        }
    }

    private fun confirmDeleteMenu() {
        val menuId = state.value.editingMenu?.id
        if (menuId == null) {
            updateLocal { it.copy(error = "No hay un menú seleccionado para eliminar") }
            return
        }
        updateLocal { it.copy(activeOverlay = null) }
        deleteMenu(menuId)
    }

    private fun openMenu(id: String) {
        val menu = state.value.menus.find { it.id == id } ?: return
        updateLocal { it.copy(editingMenu = menu, isEditing = false) }
    }

    private fun handleBack() {
        if (_state.value.isEditing) handleCancelEdit()
        else updateLocal { it.copy(editingMenu = null, isEditing = false) }
    }

    private fun saveDish(action: DigitalMenuAction.SaveDish) {
        val menu = state.value.editingMenu
        if (menu == null) {
            updateLocal { it.copy(error = "No hay un menú activo para agregar el plato") }
            return
        }
        val existingDish = (state.value.activeOverlay as? DigitalMenuOverlay.DishDialog)?.dish

        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch

            menuUseCases.upsertDish(
                userId = userId,
                menuId = menu.id,
                name = action.name,
                description = action.description,
                price = action.price,
                imageUrl = action.imageUrl,
                categoryId = action.categoryId,
                existingDish = existingDish
            )
                .onSuccess { updateLocal { it.copy(isLoading = false, activeOverlay = null) } }
                .onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                    snackbarManager.showMessage(error.message ?: "Error saving dish")
                }
        }
    }

    private fun deleteDish(dish: Dish) {
        viewModelScope.launch {
            menuUseCases.deleteDish(dish)
                .onSuccess {
                    updateLocal { it.copy(error = null) }
                    snackbarManager.showMessage("Dish deleted successfully")
                }
                .onFailure { error ->
                    updateLocal { it.copy(error = error.message) }
                }
        }
    }

    private fun saveCategory(name: String) {
        val menu = state.value.editingMenu
        if (menu == null) {
            updateLocal { it.copy(error = "No hay un menú activo para agregar la categoría") }
            return
        }
        val editing = (state.value.activeOverlay as? DigitalMenuOverlay.CategoryDialog)?.category

        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }

            val result = if (editing != null) {
                menuUseCases.updateCategory(editing.copy(name = name))
            } else {
                menuUseCases.createCategory(
                    MenuCategory(
                        id = UUID.randomUUID().toString(),
                        menuId = menu.id,
                        name = name,
                        sortOrder = 0,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }

            result
                .onSuccess { updateLocal { it.copy(isLoading = false, activeOverlay = null) } }
                .onFailure { error ->
                    updateLocal {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    private fun deleteCategory(category: MenuCategory) {
        viewModelScope.launch {
            menuUseCases.deleteCategory(category.id).onFailure { error ->
                updateLocal { it.copy(error = error.message) }
            }
        }
    }

    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.DIGITAL_MENU)
        updateLocal {
            it.copy(
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached ||
                        result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun updateLocal(update: (DigitalMenuState) -> DigitalMenuState) {
        _state.update(update)
    }

    private suspend fun sendEvent(event: DigitalMenuEvent) {
        _events.send(event)
    }
}