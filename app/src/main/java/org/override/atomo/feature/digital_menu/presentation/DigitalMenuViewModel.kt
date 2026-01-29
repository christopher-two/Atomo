/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.subscription.CanAddItemResult
import org.override.atomo.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.domain.usecase.subscription.GetServiceLimitsUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.libs.session.api.SessionRepository
import java.io.File
import java.io.FileOutputStream
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
    private val context: Context
) : ViewModel() {

    private val contentResolver = context.contentResolver

    private val _state = MutableStateFlow(DigitalMenuState())
    val state = _state
        .onStart { loadMenus() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DigitalMenuState(),
        )

    fun onAction(action: DigitalMenuAction) {
        when (action) {
            is DigitalMenuAction.CreateMenu -> createMenu()
            is DigitalMenuAction.DeleteMenu -> deleteMenu(action.id)
            is DigitalMenuAction.OpenMenu -> openMenu(action.id)
            is DigitalMenuAction.UpgradePlan -> { /* Handle navigation to pay/subscription */
            }

            // Editor Actions
            DigitalMenuAction.ToggleEditMode -> toggleEditMode()
            is DigitalMenuAction.UpdateEditingMenu -> updateEditingMenu(action.menu)
            DigitalMenuAction.SaveMenu -> saveMenu()
            DigitalMenuAction.CancelEdit -> cancelEdit()
            is DigitalMenuAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            DigitalMenuAction.Back -> handleBack()

            // Dish Actions
            is DigitalMenuAction.OpenAddDishDialog -> _state.update {
                it.copy(
                    isDishDialogVisible = true,
                    dishToEdit = null
                )
            }

            is DigitalMenuAction.OpenEditDishDialog -> _state.update {
                it.copy(
                    isDishDialogVisible = true,
                    dishToEdit = action.dish
                )
            }

            DigitalMenuAction.CloseDishDialog -> _state.update {
                it.copy(
                    isDishDialogVisible = false,
                    dishToEdit = null
                )
            }

            is DigitalMenuAction.SaveDish -> saveDish(action)
            is DigitalMenuAction.DeleteDish -> deleteDish(action.dish)

            // Delete Confirmation
            DigitalMenuAction.ShowDeleteConfirmation -> _state.update {
                it.copy(
                    isDeleteDialogVisible = true
                )
            }

            DigitalMenuAction.HideDeleteConfirmation -> _state.update {
                it.copy(
                    isDeleteDialogVisible = false
                )
            }

            DigitalMenuAction.ConfirmDelete -> confirmDeleteMenu()
        }
    }

    private fun confirmDeleteMenu() {
        val menuId = _state.value.editingMenu?.id ?: return
        _state.update { it.copy(isDeleteDialogVisible = false) }
        deleteMenu(menuId)
    }

    private fun handleBack() {
        if (_state.value.isEditing) {
            cancelEdit()
        } else if (_state.value.editingMenu != null) {
            // Close detail view
            _state.update { it.copy(editingMenu = null, isEditing = false) }
        } else {
            // Navigate back from root if needed
            // rootNavigation.back()
        }
    }

    private fun openMenu(id: String) {
        val menu = _state.value.menus.find { it.id == id } ?: return
        _state.update {
            it.copy(
                editingMenu = menu,
                isEditing = false
            )
        }
    }

    private fun toggleEditMode() {
        _state.update { state -> state.copy(isEditing = !state.isEditing) }
    }

    private fun updateEditingMenu(menu: Menu) {
        _state.update { it.copy(editingMenu = menu) }
    }

    private fun saveMenu() {
        viewModelScope.launch {
            val menu = _state.value.editingMenu ?: return@launch
            _state.update { it.copy(isLoading = true) }

            menuUseCases.updateMenu(menu).onSuccess {
                menu.dishes.forEach { dish ->
                    menuUseCases.upsertDish(dish)
                }
                _state.update { it.copy(isLoading = false, isEditing = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun cancelEdit() {
        val currentId = _state.value.editingMenu?.id ?: return
        val original = _state.value.menus.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingMenu = original) }
    }

    // Dish Handling
    private fun saveDish(action: DigitalMenuAction.SaveDish) {
        val menu = _state.value.editingMenu ?: return
        val currentDishes = menu.dishes.toMutableList()
        val editingDish = _state.value.dishToEdit

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch

            // Check limits for NEW dishes
            if (editingDish == null) {
                val canAdd = canAddDishUseCase(userId, menu.id)
                if (canAdd is CanAddItemResult.LimitReached) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Plan limit reached: ${canAdd.limit} dishes allowed."
                        )
                    }
                    // Optionally trigger upgrade dialog or similar event
                    return@launch
                } else if (canAdd is CanAddItemResult.Error) {
                    _state.update { it.copy(isLoading = false, error = canAdd.message) }
                    return@launch
                }
            }

            // Handle Image Upload if needed
            val imageUrlResult = try {
                if (action.imageUrl != null && action.imageUrl.startsWith("content://")) {
                    android.util.Log.d("Upload", "Starting image upload process for URI: ${action.imageUrl}")
                    val bytes = compressImageUris(listOf(action.imageUrl.toUri()))
                        .getOrNull()
                        ?.firstOrNull()
                        ?.toFile()
                        ?.readBytes()
                    
                    if (bytes != null) {
                        android.util.Log.d("Upload", "Image compressed successfully. Size: ${bytes.size} bytes")
                        val dishId = editingDish?.id ?: UUID.randomUUID().toString()
                        val uploadResult = uploadDishImage(userId, dishId, bytes)
                        
                        uploadResult.onFailure { e ->
                            android.util.Log.e("Upload", "Upload failed", e)
                        }
                        
                         uploadResult.getOrThrow()
                    } else {
                        android.util.Log.e("Upload", "Failed to read/compress image bytes")
                        null 
                    }
                } else {
                    action.imageUrl
                }
            } catch (e: Exception) {
                android.util.Log.e("Upload", "Exception during image processing/upload", e)
                _state.update { it.copy(isLoading = false, error = "Error uploading image: ${e.message}") }
                return@launch
            }

            if (editingDish != null) {
                // Edit existing
                val updatedDish = editingDish.copy(
                    name = action.name,
                    description = action.description,
                    price = action.price,
                    imageUrl = imageUrlResult
                )
                val index = currentDishes.indexOfFirst { it.id == editingDish.id }
                if (index != -1) {
                    currentDishes[index] = updatedDish
                }
            } else {
                // Add new
                val newDish = Dish(
                    id = UUID.randomUUID().toString(),
                    menuId = menu.id,
                    categoryId = null,
                    name = action.name,
                    description = action.description,
                    price = action.price,
                    imageUrl = imageUrlResult,
                    isVisible = true,
                    sortOrder = currentDishes.size,
                    createdAt = System.currentTimeMillis()
                )
                currentDishes.add(newDish)
            }

            val updatedMenu = menu.copy(dishes = currentDishes)
            _state.update {
                it.copy(
                    editingMenu = updatedMenu,
                    isDishDialogVisible = false,
                    dishToEdit = null,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun compressImageUris(uris: List<Uri>) = withContext(Dispatchers.IO) {
        runCatching {
            uris.map { uri ->
                val sourceFile = resolveUriToFile(uri)
                    ?: error("No se pudo leer la imagen seleccionada")

                val compressedImageFile = Compressor.compress(context, sourceFile) {
                    default(format = Bitmap.CompressFormat.JPEG)
                    resolution(1280, 1280)
                    quality(80)
                }
                Uri.fromFile(compressedImageFile)
            }
        }
    }

    private fun resolveUriToFile(uri: Uri): File? {
        // If it's already a file URI, return directly
        if (uri.scheme == "file") return runCatching { uri.toFile() }.getOrNull()

        val tempFile =
            runCatching { File.createTempFile("upload_", ".tmp", context.cacheDir) }.getOrNull()
                ?: return null

        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        }.getOrNull()
    }

    private fun deleteDish(dish: Dish) {
        val menu = _state.value.editingMenu ?: return
        val currentDishes = menu.dishes.toMutableList()
        currentDishes.remove(dish)
        val updatedMenu = menu.copy(dishes = currentDishes)
        _state.update { it.copy(editingMenu = updatedMenu) }

        viewModelScope.launch {
            // Delete image if exists
            if (dish.imageUrl != null) {
                deleteDishImage(dish.imageUrl)
            }
            menuUseCases.deleteDish(dish.id)
        }
    }


    private fun loadMenus() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first()

            if (userId == null) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            launch {
                menuUseCases.getMenus(userId).collect { list ->
                    _state.update { state ->
                        // Logic for Single Menu:
                        // If we find a menu, we automatically set it as editingMenu (view mode)
                        // This effectively bypasses the list screen.
                        val existingMenu = list.firstOrNull()

                        // If we are currently editing/viewing something, we might want to keep it updated,
                        // but if it's the *first load* (or refresh), we default to the existing menu.
                        // If no menu exists, editingMenu is null, which the UI should handle by showing Create screen.

                        val currentId = state.editingMenu?.id
                        val updatedEditing = if (currentId != null) {
                            // Update the currently viewed menu with new data from flow
                            list.find { it.id == currentId }
                                ?: (if (state.isEditing) state.editingMenu else existingMenu)
                        } else {
                            // Initial load or no menu selected yet -> auto select first
                            existingMenu
                        }

                        state.copy(menus = list, editingMenu = updatedEditing)
                    }
                    checkCreationLimit(userId)
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

    private fun createMenu() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            _state.update { it.copy(isLoading = true) }

            val result = canCreateServiceUseCase(userId, ServiceType.DIGITAL_MENU)
            if (result !is CanCreateResult.Success) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val newMenuId = UUID.randomUUID().toString()
            val newMenu = Menu(
                id = newMenuId,
                userId = userId,
                name = "My New Menu",
                description = "Digital Menu Description",
                isActive = true,
                templateId = "minimalist",
                primaryColor = "#000000",
                fontFamily = "Inter",
                logoUrl = null,
                createdAt = System.currentTimeMillis(),
                dishes = emptyList()
            )

            menuUseCases.createMenu(newMenu).onSuccess {
                _state.update {
                    it.copy(
                        editingMenu = newMenu,
                        isEditing = true,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteMenu(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            menuUseCases.deleteMenu(id).onSuccess {
                _state.update { it.copy(isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}