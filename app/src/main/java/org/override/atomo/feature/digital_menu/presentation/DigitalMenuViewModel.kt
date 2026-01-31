/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.MenuCategory
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.domain.usecase.subscription.CanAddItemResult
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.domain.usecase.subscription.GetServiceLimitsUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.libs.image.api.ImageManager
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

class DigitalMenuViewModel(
    private val sessionRepository: SessionRepository,
    private val menuUseCases: MenuUseCases,
    private val getServiceLimitsUseCase: GetServiceLimitsUseCase,
    private val subscriptionUseCases: SubscriptionUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val canAddDishUseCase: CanAddDishUseCase,
    private val uploadDishImage: UploadDishImageUseCase,
    private val deleteDishImage: DeleteDishImageUseCase,
    private val imageManager: ImageManager,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private var menuBeforeEdit: Menu? = null

    private val _state = MutableStateFlow(DigitalMenuState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DigitalMenuState(),
        )

    private val _events = Channel<DigitalMenuEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadMenus()
    }

    fun onAction(action: DigitalMenuAction) {
        when (action) {
            is DigitalMenuAction.CreateMenu -> createMenu()
            is DigitalMenuAction.DeleteMenu -> deleteMenu(action.id)
            is DigitalMenuAction.OpenMenu -> openMenu(action.id)
            is DigitalMenuAction.UpgradePlan -> {}
            DigitalMenuAction.ToggleEditMode -> toggleEditMode()
            is DigitalMenuAction.UpdateEditingMenu -> updateEditingMenu(action.menu)
            DigitalMenuAction.SaveMenu -> saveMenu()
            DigitalMenuAction.CancelEdit -> handleCancelEdit()
            is DigitalMenuAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            DigitalMenuAction.Back -> handleBack()

            // Dish Actions
            DigitalMenuAction.OpenAddDishDialog -> _state.update { it.copy(isDishDialogVisible = true, dishToEdit = null) }
            is DigitalMenuAction.OpenEditDishDialog -> _state.update { it.copy(isDishDialogVisible = true, dishToEdit = action.dish) }
            DigitalMenuAction.CloseDishDialog -> _state.update { it.copy(isDishDialogVisible = false, dishToEdit = null) }
            is DigitalMenuAction.SaveDish -> saveDish(action)
            is DigitalMenuAction.DeleteDish -> deleteDish(action.dish)

            // Category Actions
            DigitalMenuAction.OpenAddCategoryDialog -> _state.update { it.copy(isCategoryDialogVisible = true, categoryToEdit = null) }
            is DigitalMenuAction.OpenEditCategoryDialog -> _state.update { it.copy(isCategoryDialogVisible = true, categoryToEdit = action.category) }
            DigitalMenuAction.CloseCategoryDialog -> _state.update { it.copy(isCategoryDialogVisible = false, categoryToEdit = null) }
            is DigitalMenuAction.SaveCategory -> saveCategory(action.name)
            is DigitalMenuAction.DeleteCategory -> deleteCategory(action.category)

            // Delete Confirmation
            DigitalMenuAction.ShowDeleteConfirmation -> _state.update { it.copy(isDeleteDialogVisible = true) }
            DigitalMenuAction.HideDeleteConfirmation -> _state.update { it.copy(isDeleteDialogVisible = false) }
            DigitalMenuAction.ConfirmDelete -> confirmDeleteMenu()

            // Discard Changes Confirmation
            DigitalMenuAction.ShowDiscardConfirmation -> _state.update { it.copy(isDiscardDialogVisible = true) }
            DigitalMenuAction.HideDiscardConfirmation -> _state.update { it.copy(isDiscardDialogVisible = false) }
            DigitalMenuAction.ConfirmDiscard -> confirmDiscard()
        }
    }

    private fun updateEditingMenu(menu: Menu) {
        _state.update { 
            it.copy(
                editingMenu = menu,
                hasUnsavedChanges = menu != menuBeforeEdit
            )
        }
    }

    private fun handleCancelEdit() {
        if (_state.value.hasUnsavedChanges) {
            _state.update { it.copy(isDiscardDialogVisible = true) }
        } else {
            confirmDiscard()
        }
    }

    private fun confirmDiscard() {
        _state.update { 
            it.copy(
                isEditing = false,
                editingMenu = menuBeforeEdit,
                hasUnsavedChanges = false,
                isDiscardDialogVisible = false
            )
        }
        menuBeforeEdit = null
    }

    private suspend fun sendEvent(event: DigitalMenuEvent) {
        _events.send(event)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadMenus() {
        viewModelScope.launch {
            sessionRepository.getCurrentUserId()
                .filterNotNull()
                .flatMapLatest { userId ->
                    checkCreationLimit(userId)
                    menuUseCases.getMenus(userId)
                }
                .collect { list ->
                    val existingMenu = list.firstOrNull()
                    _state.update { state ->
                        val currentId = state.editingMenu?.id
                        val updatedEditing = if (currentId != null) {
                            list.find { it.id == currentId } ?: (if (state.isEditing) state.editingMenu else existingMenu)
                        } else {
                            existingMenu
                        }
                        // Ensure isLoading is disabled once data arrives
                        state.copy(menus = list, editingMenu = updatedEditing, isLoading = false)
                    }
                }
        }
    }

    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.DIGITAL_MENU)
        _state.update {
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun saveMenu() {
        val menu = _state.value.editingMenu ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            menuUseCases.updateMenu(menu).onSuccess {
                // Save all categories
                val categoryResults = menu.categories.map { menuUseCases.updateCategory(it) }
                val categoryFailures = categoryResults.filter { it.isFailure }
                
                // Save all dishes
                val dishResults = menu.dishes.map { menuUseCases.upsertDish(it) }
                val dishFailures = dishResults.filter { it.isFailure }
                
                // Check if all saves succeeded
                if (categoryFailures.isEmpty() && dishFailures.isEmpty()) {
                    _state.update { it.copy(isLoading = false, isEditing = false, hasUnsavedChanges = false) }
                    sendEvent(DigitalMenuEvent.MenuSaved)
                } else {
                    val errorMessages = mutableListOf<String>()
                    if (categoryFailures.isNotEmpty()) {
                        errorMessages.add("${categoryFailures.size} categories failed to save")
                    }
                    if (dishFailures.isNotEmpty()) {
                        errorMessages.add("${dishFailures.size} dishes failed to save")
                    }
                    _state.update { it.copy(isLoading = false, error = errorMessages.joinToString(", ")) }
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun saveDish(action: DigitalMenuAction.SaveDish) {
        val menu = _state.value.editingMenu ?: return
        val editingDish = _state.value.dishToEdit

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch

            if (editingDish == null) {
                val canAdd = canAddDishUseCase(userId, menu.id)
                if (canAdd is CanAddItemResult.LimitReached) {
                    _state.update { it.copy(isLoading = false) }
                    snackbarManager.showMessage("Limit reached: ${canAdd.limit} dishes.")
                    return@launch
                }
            }

            val imageUrlResult = try {
                if (action.imageUrl != null && action.imageUrl.startsWith("content://")) {
                    val uri = action.imageUrl.toUri()
                    val bytes = imageManager.compressImage(uri).getOrThrow()
                    uploadDishImage(userId, editingDish?.id ?: UUID.randomUUID().toString(), bytes).getOrThrow()
                } else action.imageUrl
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage("Image error: ${e.message}")
                return@launch
            }

            val currentDishes = menu.dishes.toMutableList()
            if (editingDish != null) {
                val index = currentDishes.indexOfFirst { it.id == editingDish.id }
                if (index != -1) currentDishes[index] = editingDish.copy(name = action.name, description = action.description, price = action.price, imageUrl = imageUrlResult, categoryId = action.categoryId)
            } else {
                currentDishes.add(Dish(UUID.randomUUID().toString(), menu.id, action.categoryId, action.name, action.description, action.price, imageUrlResult, true, currentDishes.size, System.currentTimeMillis()))
            }

            _state.update { it.copy(editingMenu = menu.copy(dishes = currentDishes), isDishDialogVisible = false, dishToEdit = null, isLoading = false, hasUnsavedChanges = true) }
        }
    }

    private fun deleteDish(dish: Dish) {
        val menu = _state.value.editingMenu ?: return
        val currentDishes = menu.dishes.toMutableList().apply { remove(dish) }
        _state.update { it.copy(editingMenu = menu.copy(dishes = currentDishes), hasUnsavedChanges = true) }
        viewModelScope.launch {
            if (dish.imageUrl != null) deleteDishImage(dish.imageUrl)
            menuUseCases.deleteDish(dish.id)
        }
    }

    private fun saveCategory(name: String) {
        val menu = _state.value.editingMenu ?: return
        val currentCategories = menu.categories.toMutableList()
        val editingCategory = _state.value.categoryToEdit

        if (editingCategory != null) {
            val index = currentCategories.indexOfFirst { it.id == editingCategory.id }
            if (index != -1) currentCategories[index] = editingCategory.copy(name = name)
        } else {
            currentCategories.add(MenuCategory(UUID.randomUUID().toString(), menu.id, name, currentCategories.size, System.currentTimeMillis()))
        }

        _state.update { it.copy(editingMenu = menu.copy(categories = currentCategories), isCategoryDialogVisible = false, categoryToEdit = null, hasUnsavedChanges = true) }
    }

    private fun deleteCategory(category: MenuCategory) {
        val menu = _state.value.editingMenu ?: return
        val currentCategories = menu.categories.toMutableList().apply { remove(category) }
        val currentDishes = menu.dishes.map { if (it.categoryId == category.id) it.copy(categoryId = null) else it }
        _state.update { it.copy(editingMenu = menu.copy(categories = currentCategories, dishes = currentDishes), hasUnsavedChanges = true) }
        viewModelScope.launch { menuUseCases.deleteCategory(category.id) }
    }

    private fun createMenu() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            _state.update { it.copy(isLoading = true) }
            if (canCreateServiceUseCase(userId, ServiceType.DIGITAL_MENU) !is CanCreateResult.Success) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val newMenu = Menu(UUID.randomUUID().toString(), userId, "My New Menu", "Digital Menu Description", true, "minimalist", "#000000", "Inter", null, System.currentTimeMillis())
            menuUseCases.createMenu(newMenu).onSuccess {
                _state.update { it.copy(editingMenu = newMenu, isEditing = true, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteMenu(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            menuUseCases.deleteMenu(id).onSuccess {
                _state.update { it.copy(isLoading = false, editingMenu = null) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun confirmDeleteMenu() {
        val menuId = _state.value.editingMenu?.id ?: return
        _state.update { it.copy(isDeleteDialogVisible = false) }
        deleteMenu(menuId)
    }

    private fun handleBack() {
        if (_state.value.isEditing) cancelEdit()
        else if (_state.value.editingMenu != null) _state.update { it.copy(editingMenu = null, isEditing = false) }
    }

    private fun openMenu(id: String) {
        val menu = _state.value.menus.find { it.id == id } ?: return
        _state.update { it.copy(editingMenu = menu, isEditing = false) }
    }

    private fun toggleEditMode() {
        menuBeforeEdit = _state.value.editingMenu
        _state.update { it.copy(isEditing = !it.isEditing, hasUnsavedChanges = false) }
    }

    private fun cancelEdit() {
        val currentId = _state.value.editingMenu?.id ?: return
        val original = _state.value.menus.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingMenu = original) }
    }
}
