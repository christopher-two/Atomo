/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.cv.domain.model.Cv
import org.override.atomo.feature.cv.domain.repository.CvRepository
import org.override.atomo.feature.cv.domain.usecase.cv.CvUseCases
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.digital_menu.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.invitation.domain.model.Invitation
import org.override.atomo.feature.invitation.domain.repository.InvitationRepository
import org.override.atomo.feature.invitation.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.portfolio.domain.model.Portfolio
import org.override.atomo.feature.portfolio.domain.repository.PortfolioRepository
import org.override.atomo.feature.portfolio.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.feature.profile.domain.ProfileValidator
import org.override.atomo.feature.profile.domain.repository.ProfileRepository
import org.override.atomo.feature.profile.domain.usecase.profile.ProfileUseCases
import org.override.atomo.feature.session.domain.repository.SessionRepository
import org.override.atomo.feature.shop.domain.model.Shop
import org.override.atomo.feature.shop.domain.repository.ShopRepository
import org.override.atomo.feature.shop.domain.usecase.shop.ShopUseCases
import java.util.UUID

/**
 * ViewModel for the Onboarding feature.
 * Manages the wizard flow for completing user profile and creating first service.
 */
class OnboardingViewModel(
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    // Repositories for direct sync
    private val profileRepository: ProfileRepository,
    private val menuRepository: MenuRepository,
    private val sessionRepository: SessionRepository,
    private val rootNavigation: RootNavigation,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private var hasLoadedInitialData = false
    private var checkUsernameJob: Job? = null

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OnboardingState()
        )

    /**
     * Handles user actions.
     */
    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.NextStep -> nextStep()
            OnboardingAction.PreviousStep -> previousStep()
            is OnboardingAction.UpdateDisplayName -> {
                _state.update { it.copy(displayName = action.name) }
            }

            is OnboardingAction.UpdateUsername -> updateUsername(action.username)
            is OnboardingAction.UpdateServiceName -> {
                _state.update { it.copy(serviceName = action.name) }
            }

            is OnboardingAction.SelectTemplate -> {
                _state.update { it.copy(selectedTemplateId = action.templateId) }
            }

            is OnboardingAction.AddCategory -> {
                _state.update { it.copy(categories = it.categories + action.name) }
            }

            is OnboardingAction.RemoveCategory -> {
                _state.update { 
                    it.copy(
                        categories = it.categories.filter { cat -> cat != action.name },
                        dishes = it.dishes.filter { dish -> dish.categoryName != action.name }
                    ) 
                }
            }

            is OnboardingAction.AddDish -> {
                _state.update { it.copy(dishes = it.dishes + action.dish) }
            }

            is OnboardingAction.RemoveDish -> {
                _state.update { it.copy(dishes = it.dishes.filter { dish -> dish != action.dish }) }
            }

            is OnboardingAction.UpdateServiceName -> {
                _state.update { it.copy(serviceName = action.name) }
            }

            OnboardingAction.FinishOnboarding -> finishOnboarding()
            OnboardingAction.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            _state.update { it.copy(isLoading = true) }

            val profile = profileUseCases.getProfile(userId).firstOrNull()
            _state.update {
                it.copy(
                    isLoading = false,
                    profile = profile,
                    displayName = profile?.displayName.orEmpty(),
                    username = profile?.username.orEmpty()
                )
            }
            
            menuUseCases.getMenuTemplates().collect { templates ->
                _state.update { 
                    it.copy(
                        templates = templates, 
                        selectedTemplateId = if (it.selectedTemplateId == null) templates.firstOrNull()?.id else it.selectedTemplateId 
                    ) 
                }
            }
        }
    }

    private fun updateUsername(username: String) {
        val formatted = ProfileValidator.formatUsername(username)

        if (!ProfileValidator.isValidUsername(formatted)) {
            _state.update {
                it.copy(
                    username = formatted,
                    usernameError = "Solo letras minúsculas, números, _ y -",
                    isUsernameAvailable = false
                )
            }
            return
        }

        _state.update {
            it.copy(
                username = formatted,
                usernameError = null,
                isCheckingUsername = true
            )
        }

        // Debounce the username check
        checkUsernameJob?.cancel()
        checkUsernameJob = viewModelScope.launch {
            delay(500)

            // If it's the same as current profile username, it's valid
            if (formatted == state.value.profile?.username) {
                _state.update {
                    it.copy(isCheckingUsername = false, isUsernameAvailable = true)
                }
                return@launch
            }

            val isAvailable = profileUseCases.checkUsernameAvailability(formatted)
            _state.update {
                it.copy(
                    isCheckingUsername = false,
                    isUsernameAvailable = isAvailable,
                    usernameError = if (isAvailable) null else "Este username ya está en uso"
                )
            }
        }
    }

    private fun nextStep() {
        val currentStep = state.value.step
        val nextStep = when (currentStep) {
            OnboardingStep.PROFILE -> OnboardingStep.MENU_DETAILS
            OnboardingStep.MENU_DETAILS -> OnboardingStep.TEMPLATE_SELECTION
            OnboardingStep.TEMPLATE_SELECTION -> OnboardingStep.MENU_ITEMS
            OnboardingStep.MENU_ITEMS -> OnboardingStep.REVIEW
            OnboardingStep.REVIEW -> return // Already at last step
        }
        _state.update { it.copy(step = nextStep) }
    }

    private fun previousStep() {
        val currentStep = state.value.step
        val prevStep = when (currentStep) {
            OnboardingStep.PROFILE -> return // Already at first step
            OnboardingStep.MENU_DETAILS -> OnboardingStep.PROFILE
            OnboardingStep.TEMPLATE_SELECTION -> OnboardingStep.MENU_DETAILS
            OnboardingStep.MENU_ITEMS -> OnboardingStep.TEMPLATE_SELECTION
            OnboardingStep.REVIEW -> OnboardingStep.MENU_ITEMS
        }
        _state.update { it.copy(step = prevStep) }
    }

    private fun finishOnboarding() {
        val currentState = state.value
        val currentProfile = currentState.profile ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch

            // 1. Update profile locally
            val updatedProfile = currentProfile.copy(
                displayName = currentState.displayName,
                username = currentState.username,
                updatedAt = System.currentTimeMillis()
            )

            val profileResult = profileUseCases.updateProfile(updatedProfile)
            if (profileResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = profileResult.exceptionOrNull()?.message
                            ?: "Error al guardar perfil"
                    )
                }
                return@launch
            }

            // 2. Create service locally
            val serviceResult = createService(userId, currentState)
            if (serviceResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = serviceResult.exceptionOrNull()?.message
                            ?: "Error al crear servicio"
                    )
                }
                return@launch
            }

            // 3. Sync profile to remote immediately
            val profileSyncResult = profileRepository.syncUp(userId)
            if (profileSyncResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al sincronizar perfil: ${profileSyncResult.exceptionOrNull()?.message}"
                    )
                }
                return@launch
            }

            // 4. Sync service to remote immediately
            val serviceSyncResult = syncService(userId)
            if (serviceSyncResult.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al sincronizar servicio: ${serviceSyncResult.exceptionOrNull()?.message}"
                    )
                }
                return@launch
            }

            // 5. Navigate to Home
            _state.update { it.copy(isLoading = false) }
            snackbarManager.showMessage("¡Bienvenido a Átomo!")
            rootNavigation.replaceWith(RouteApp.Home)
        }
    }

    private suspend fun syncService(userId: String): Result<Unit> {
        return menuRepository.syncUp(userId)
    }

    private suspend fun createService(userId: String, state: OnboardingState): Result<Unit> {
        val serviceName = state.serviceName
        val now = System.currentTimeMillis()

        // Default theme values
        val defaultPrimaryColor = "#000000"
        val defaultFontFamily = "Inter"
        val templateId = state.selectedTemplateId ?: "minimalist"
        val menuId = UUID.randomUUID().toString()

        val result = menuUseCases.createMenu(
            Menu(
                id = menuId,
                userId = userId,
                name = serviceName,
                description = null,
                isActive = true,
                templateId = templateId,
                primaryColor = defaultPrimaryColor,
                fontFamily = defaultFontFamily,
                logoUrl = null,
                createdAt = now
            )
        )
        if (result.isFailure) return result.map { }
        
        // Map Categories to specific UUIDs
        val categoryIdMap = mutableMapOf<String, String>()
        state.categories.forEachIndexed { index, name ->
            val catId = UUID.randomUUID().toString()
            categoryIdMap[name] = catId
            menuUseCases.createCategory(
                org.override.atomo.feature.digital_menu.domain.model.MenuCategory(
                    id = catId,
                    menuId = menuId,
                    name = name,
                    sortOrder = index,
                    createdAt = now
                )
            )
        }
        
        // Add Dishes mapped to Category UUIDs
        state.dishes.forEachIndexed { index, dishInput ->
            val catId = categoryIdMap[dishInput.categoryName] ?: return@forEachIndexed
            menuUseCases.createDish(
                org.override.atomo.feature.digital_menu.domain.model.Dish(
                    id = UUID.randomUUID().toString(),
                    menuId = menuId,
                    categoryId = catId,
                    name = dishInput.name,
                    description = dishInput.description,
                    price = dishInput.price,
                    imageUrl = null,
                    isVisible = true,
                    sortOrder = index,
                    createdAt = now
                )
            )
        }
        return Result.success(Unit)
    }
}
