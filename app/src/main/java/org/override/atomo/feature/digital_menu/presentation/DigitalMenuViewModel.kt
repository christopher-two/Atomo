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
import kotlinx.coroutines.flow.combine
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
import org.override.atomo.libs.image.api.ImageManager
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

/**
 * ViewModel for Digital Menu feature.
 * Refactored to use reactive state management and clearer separation of concerns.
 */
class DigitalMenuViewModel(
    private val sessionRepository: SessionRepository,
    private val menuUseCases: MenuUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val canAddDishUseCase: CanAddDishUseCase,
    private val uploadDishImage: UploadDishImageUseCase,
    private val deleteDishImage: DeleteDishImageUseCase,
    private val imageManager: ImageManager,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private var menuBeforeEdit: Menu? = null

    // Local UI State (Dialogs, Editor flags, etc.)
    private val _localState = MutableStateFlow(LocalUiState())

    // Repository Data Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    private val menusFlow = sessionRepository.getCurrentUserId()
        .filterNotNull()
        .flatMapLatest { userId ->
            // Side effect: Check creation limit when userId is available
            checkCreationLimit(userId)
            menuUseCases.getMenus(userId)
        }

    // Combined State
    val state = combine(
        menusFlow,
        _localState
    ) { menus, local ->
        val existingMenu = menus.firstOrNull()

        // Determine currently displayed menu (Editing vs Viewing)
        val activeMenu = if (local.isEditing && local.editingMenu != null) {
            local.editingMenu
        } else {
            existingMenu
        }

        // Calculate changes
        val hasChanges =
            if (local.isEditing && local.editingMenu != null && menuBeforeEdit != null) {
                local.editingMenu != menuBeforeEdit
            } else {
                false
            }

        DigitalMenuState(
            isLoading = local.isLoading,
            menus = menus,
            error = local.error,
            canCreate = local.canCreate,
            limitReached = local.limitReached,

            // Editor
            isEditing = local.isEditing,
            editingMenu = activeMenu,
            hasUnsavedChanges = hasChanges,
            showPreviewSheet = local.showPreviewSheet,

            // Dialogs
            isDishDialogVisible = local.isDishDialogVisible,
            dishToEdit = local.dishToEdit,
            isCategoryDialogVisible = local.isCategoryDialogVisible,
            categoryToEdit = local.categoryToEdit,
            isDiscardDialogVisible = local.isDiscardDialogVisible,
            isDeleteDialogVisible = local.isDeleteDialogVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DigitalMenuState(isLoading = true)
    )

    private val _events = Channel<DigitalMenuEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: DigitalMenuAction) {
        when (action) {
            // High-level Actions
            is DigitalMenuAction.CreateMenu -> createMenu()
            is DigitalMenuAction.DeleteMenu -> deleteMenu(action.id)
            is DigitalMenuAction.OpenMenu -> openMenu(action.id)
            is DigitalMenuAction.UpgradePlan -> { /* Navigate to Upgrade */
            }

            is DigitalMenuAction.TogglePreviewSheet -> updateLocal { it.copy(showPreviewSheet = action.show) }
            DigitalMenuAction.Back -> handleBack()

            // Editor Lifecycle
            DigitalMenuAction.ToggleEditMode -> toggleEditMode()
            is DigitalMenuAction.UpdateEditingMenu -> updateLocalEditingMenu(action.menu)
            DigitalMenuAction.SaveMenu -> saveMenu()
            DigitalMenuAction.CancelEdit -> handleCancelEdit()

            // Confirmation Dialogs
            DigitalMenuAction.ShowDeleteConfirmation -> updateLocal { it.copy(isDeleteDialogVisible = true) }
            DigitalMenuAction.HideDeleteConfirmation -> updateLocal { it.copy(isDeleteDialogVisible = false) }
            DigitalMenuAction.ConfirmDelete -> confirmDeleteMenu()
            DigitalMenuAction.ShowDiscardConfirmation -> updateLocal {
                it.copy(
                    isDiscardDialogVisible = true
                )
            }

            DigitalMenuAction.HideDiscardConfirmation -> updateLocal {
                it.copy(
                    isDiscardDialogVisible = false
                )
            }

            DigitalMenuAction.ConfirmDiscard -> confirmDiscard()

            // Sub-handlers
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

    // region Action Handlers

    private fun handleDishAction(action: DigitalMenuAction) {
        when (action) {
            DigitalMenuAction.OpenAddDishDialog -> updateLocal {
                it.copy(
                    isDishDialogVisible = true,
                    dishToEdit = null
                )
            }

            is DigitalMenuAction.OpenEditDishDialog -> updateLocal {
                it.copy(
                    isDishDialogVisible = true,
                    dishToEdit = action.dish
                )
            }

            DigitalMenuAction.CloseDishDialog -> updateLocal {
                it.copy(
                    isDishDialogVisible = false,
                    dishToEdit = null
                )
            }
            is DigitalMenuAction.SaveDish -> saveDish(action)
            is DigitalMenuAction.DeleteDish -> deleteDish(action.dish)
            else -> Unit
        }
    }

    private fun handleCategoryAction(action: DigitalMenuAction) {
        when (action) {
            DigitalMenuAction.OpenAddCategoryDialog -> updateLocal {
                it.copy(
                    isCategoryDialogVisible = true,
                    categoryToEdit = null
                )
            }

            is DigitalMenuAction.OpenEditCategoryDialog -> updateLocal {
                it.copy(
                    isCategoryDialogVisible = true,
                    categoryToEdit = action.category
                )
            }

            DigitalMenuAction.CloseCategoryDialog -> updateLocal {
                it.copy(
                    isCategoryDialogVisible = false,
                    categoryToEdit = null
                )
            }
            is DigitalMenuAction.SaveCategory -> saveCategory(action.name)
            is DigitalMenuAction.DeleteCategory -> deleteCategory(action.category)
            else -> Unit
        }
    }

    // endregion

    // region Menu Logic

    private fun createMenu() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            updateLocal { it.copy(isLoading = true) }

            if (canCreateServiceUseCase(
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
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis()
            )

            menuUseCases.createMenu(newMenu).onSuccess {
                // Determine if we should enter edit mode immediately?
                // For now, just stop loading and let flow update display
                updateLocal { it.copy(isLoading = false, editingMenu = newMenu, isEditing = true) }
                menuBeforeEdit = newMenu
            }.onFailure { error ->
                updateLocal { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun saveMenu() {
        val menu = _localState.value.editingMenu ?: return
        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            menuUseCases.updateMenu(menu).onSuccess {
                // Also save children changes (Optimistic updates in local state need to be persisted)
                menu.categories.forEach { menuUseCases.updateCategory(it) }
                menu.dishes.forEach { menuUseCases.upsertDish(it) }

                updateLocal { it.copy(isLoading = false, isEditing = false) }
                menuBeforeEdit = null
                sendEvent(DigitalMenuEvent.MenuSaved)
            }.onFailure { error ->
                updateLocal { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteMenu(id: String) {
        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            menuUseCases.deleteMenu(id).onSuccess {
                updateLocal { it.copy(isLoading = false, editingMenu = null, isEditing = false) }
            }.onFailure { error ->
                updateLocal { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun updateEditingMenu(menu: Menu) {
        updateLocal { it.copy(editingMenu = menu) }
    }

    private fun toggleEditMode() {
        val current = _localState.value
        if (!current.isEditing) {
            // Start Editing
            val active = state.value.editingMenu // From flow
            menuBeforeEdit = active
            updateLocal { it.copy(isEditing = true, editingMenu = active) }
        } else {
            // Stop Editing (Save check is done in UI or via Cancel)
            // This toggle is usually for "Start", but if used for "Stop" acts as Cancel?
            // Assuming simplified toggle:
            handleCancelEdit()
        }
    }

    private fun handleCancelEdit() {
        if (state.value.hasUnsavedChanges) {
            updateLocal { it.copy(isDiscardDialogVisible = true) }
        } else {
            confirmDiscard()
        }
    }

    private fun confirmDiscard() {
        updateLocal { 
            it.copy(
                isEditing = false,
                editingMenu = null,
                isDiscardDialogVisible = false
            )
        }
        menuBeforeEdit = null
    }

    // endregion

    // region Dish Logic

    private fun saveDish(action: DigitalMenuAction.SaveDish) {
        // Use the displayed menu (either editing or active) to get the ID
        val menu = state.value.editingMenu ?: return

        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch

            // Limit Check for NEW dishes
            if (state.value.dishToEdit == null) {
                val canAdd = canAddDishUseCase(userId, menu.id)
                if (canAdd is CanAddItemResult.LimitReached) {
                    updateLocal { it.copy(isLoading = false) }
                    snackbarManager.showMessage("Limit reached: ${canAdd.limit} dishes.")
                    return@launch
                }
            }

            // Image Upload
            val imageUrlResult = try {
                if (action.imageUrl != null && action.imageUrl.startsWith("content://")) {
                    val uri = action.imageUrl.toUri()
                    val bytes = imageManager.compressImage(uri).getOrThrow()
                    val dishId = state.value.dishToEdit?.id ?: UUID.randomUUID().toString()
                    uploadDishImage(userId, dishId, bytes).getOrThrow()
                } else action.imageUrl
            } catch (e: Exception) {
                updateLocal { it.copy(isLoading = false) }
                snackbarManager.showMessage("Image error: ${e.message}")
                return@launch
            }

            val dishToSave = Dish(
                id = state.value.dishToEdit?.id ?: UUID.randomUUID().toString(),
                menuId = menu.id,
                categoryId = action.categoryId,
                name = action.name,
                description = action.description,
                price = action.price,
                imageUrl = imageUrlResult,
                isVisible = true,
                sortOrder = state.value.dishToEdit?.sortOrder
                    ?: 0, // Should be calculated if we care about detailed order
                createdAt = state.value.dishToEdit?.createdAt ?: System.currentTimeMillis()
            )

            menuUseCases.upsertDish(dishToSave).onSuccess {
                updateLocal {
                    it.copy(
                        isDishDialogVisible = false,
                        dishToEdit = null,
                        isLoading = false
                    )
                }
                // Note: We don't set hasUnsavedChanges because this is persisted immediately
            }.onFailure { error ->
                updateLocal { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteDish(dish: Dish) {
        viewModelScope.launch {
            // Immediate delete
            if (dish.imageUrl != null) deleteDishImage(dish.imageUrl)
            menuUseCases.deleteDish(dish.id).onFailure { error ->
                updateLocal { it.copy(error = error.message) }
            }
        }
    }

    // endregion

    // region Category Logic

    private fun saveCategory(name: String) {
        val menu = state.value.editingMenu ?: return
        val editingCategory = state.value.categoryToEdit

        viewModelScope.launch {
            updateLocal { it.copy(isLoading = true) }

            if (editingCategory != null) {
                // Update
                val updated = editingCategory.copy(name = name)
                menuUseCases.updateCategory(updated).onSuccess {
                    updateLocal {
                        it.copy(
                            isCategoryDialogVisible = false,
                            categoryToEdit = null,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                }
            } else {
                // Create
                val newCategory = MenuCategory(
                    id = UUID.randomUUID().toString(),
                    menuId = menu.id,
                    name = name,
                    sortOrder = 0, // Logic for order needed if important
                    createdAt = System.currentTimeMillis()
                )
                menuUseCases.createCategory(newCategory).onSuccess {
                    updateLocal {
                        it.copy(
                            isCategoryDialogVisible = false,
                            categoryToEdit = null,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    updateLocal { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    private fun deleteCategory(category: MenuCategory) {
        viewModelScope.launch {
            // Immediate delete
            menuUseCases.deleteCategory(category.id).onFailure { error ->
                updateLocal { it.copy(error = error.message) }
            }
        }
    }

    // endregion

    // region Navigation & Helpers

    private fun openMenu(id: String) {
        val menu = state.value.menus.find { it.id == id } ?: return
        updateLocal { it.copy(editingMenu = menu, isEditing = false) }
    }

    private fun updateLocalEditingMenu(menu: Menu) {
        updateLocal { it.copy(editingMenu = menu) }
    }

    private fun handleBack() {
        if (_localState.value.isEditing) handleCancelEdit()
        else updateLocal { it.copy(editingMenu = null, isEditing = false) }
    }

    private fun confirmDeleteMenu() {
        val menuId = _localState.value.editingMenu?.id ?: return
        updateLocal { it.copy(isDeleteDialogVisible = false) }
        deleteMenu(menuId)
    }

    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.DIGITAL_MENU)
        updateLocal {
            it.copy(
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun updateLocal(update: (LocalUiState) -> LocalUiState) {
        _localState.update(update)
    }

    private suspend fun sendEvent(event: DigitalMenuEvent) {
        _events.send(event)
    }

    // Internal State Helper
    private data class LocalUiState(
        val isLoading: Boolean = false,
        val isEditing: Boolean = false,
        val editingMenu: Menu? = null,
        val error: String? = null,
        val canCreate: Boolean = false,
        val limitReached: Boolean = false,
        val showPreviewSheet: Boolean = false,
        val isDishDialogVisible: Boolean = false,
        val dishToEdit: Dish? = null,
        val isCategoryDialogVisible: Boolean = false,
        val categoryToEdit: MenuCategory? = null,
        val isDiscardDialogVisible: Boolean = false,
        val isDeleteDialogVisible: Boolean = false
    )
    // endregion
}
