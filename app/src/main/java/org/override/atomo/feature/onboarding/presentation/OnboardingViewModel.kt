/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import android.util.Log
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
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.digital_menu.domain.usecase.menu.MenuUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.profile.domain.ProfileValidator
import org.override.atomo.feature.profile.domain.repository.ProfileRepository
import org.override.atomo.feature.profile.domain.usecase.profile.ProfileUseCases
import org.override.atomo.feature.session.domain.repository.SessionRepository
import org.override.atomo.feature.sync.data.manager.SyncManager
import java.util.UUID

class OnboardingViewModel(
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val profileRepository: ProfileRepository,
    private val menuRepository: MenuRepository,
    private val sessionRepository: SessionRepository,
    private val rootNavigation: RootNavigation,
    private val snackbarManager: SnackbarManager,
    private val syncManager: SyncManager
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
            is OnboardingAction.UpdateSocialLink -> {
                _state.update {
                    it.copy(socialLinks = it.socialLinks + (action.platform to action.url))
                }
            }
            is OnboardingAction.FormatSocialLink -> formatSocialLink(action.platform)
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
            OnboardingAction.FinishOnboarding -> finishOnboarding()
            OnboardingAction.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun formatSocialLink(platform: String) {
        val currentLinks = _state.value.socialLinks.toMutableMap()
        val currentUrl = currentLinks[platform] ?: return
        val formatted = formatSocialUrl(platform, currentUrl)
        if (formatted != currentUrl) {
            currentLinks[platform] = formatted
            _state.update { it.copy(socialLinks = currentLinks) }
        }
    }

    private fun formatSocialUrl(platform: String, input: String): String {
        if (input.isBlank()) return ""
        val isUsername = !input.contains(".") && !input.contains("/")
        if (isUsername) {
            val baseUrl = when (platform.lowercase()) {
                "instagram" -> "https://instagram.com/"
                "twitter", "x" -> "https://x.com/"
                "linkedin" -> "https://linkedin.com/in/"
                "github" -> "https://github.com/"
                "facebook" -> "https://facebook.com/"
                "tiktok" -> "https://tiktok.com/@"
                else -> ""
            }
            if (baseUrl.isNotEmpty()) return "$baseUrl$input"
        }
        return if (ProfileValidator.isValidUrl(input) && input.contains(".")) {
            input
        } else {
            if (!input.startsWith("http://") && !input.startsWith("https://") && input.contains(".")) {
                "https://$input"
            } else {
                input
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
        }

        // Collect templates in its own coroutine (Flow de Room nunca completa)
        viewModelScope.launch {
            menuUseCases.getMenuTemplates().collect { templates ->
                _state.update {
                    it.copy(
                        templates = templates,
                        selectedTemplateId = it.selectedTemplateId ?: templates.firstOrNull()?.id
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
            OnboardingStep.PROFILE -> OnboardingStep.SOCIAL_LINKS
            OnboardingStep.SOCIAL_LINKS -> OnboardingStep.MENU_DETAILS
            OnboardingStep.MENU_DETAILS -> OnboardingStep.TEMPLATE_SELECTION
            OnboardingStep.TEMPLATE_SELECTION -> OnboardingStep.MENU_ITEMS
            OnboardingStep.MENU_ITEMS -> OnboardingStep.REVIEW
            OnboardingStep.REVIEW -> return
        }
        _state.update { it.copy(step = nextStep) }
    }

    private fun previousStep() {
        val currentStep = state.value.step
        val prevStep = when (currentStep) {
            OnboardingStep.PROFILE -> return
            OnboardingStep.SOCIAL_LINKS -> OnboardingStep.PROFILE
            OnboardingStep.MENU_DETAILS -> OnboardingStep.SOCIAL_LINKS
            OnboardingStep.TEMPLATE_SELECTION -> OnboardingStep.MENU_DETAILS
            OnboardingStep.MENU_ITEMS -> OnboardingStep.TEMPLATE_SELECTION
            OnboardingStep.REVIEW -> OnboardingStep.MENU_ITEMS
        }
        _state.update { it.copy(step = prevStep) }
    }

    private fun finishOnboarding() {
        val currentState = state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            Log.d(TAG, "finishOnboarding: START — userId lookup...")

            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: run {
                Log.e(TAG, "finishOnboarding: ABORT — userId is null, no active session")
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage("Error: Sesión no encontrada")
                return@launch
            }
            Log.d(TAG, "finishOnboarding: userId=$userId")

            // Cancel any in-flight UploadWorker BEFORE local writes to avoid race conditions
            syncManager.cancelUploadWorker(userId)
            Log.d(TAG, "finishOnboarding: UploadWorker cancelled")

            // 1. Update profile locally
            val updatedProfile = currentState.profile?.copy(
                displayName = currentState.displayName,
                username = currentState.username,
                socialLinks = currentState.socialLinks.ifEmpty { null },
                updatedAt = System.currentTimeMillis()
            ) ?: org.override.atomo.feature.profile.domain.model.Profile(
                id = userId,
                username = currentState.username,
                displayName = currentState.displayName,
                avatarUrl = null,
                socialLinks = currentState.socialLinks.ifEmpty { null },
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            Log.d(TAG, "finishOnboarding: [1] Saving profile locally — username=${updatedProfile.username}")
            val profileResult = profileUseCases.updateProfile(updatedProfile)
            if (profileResult.isFailure) {
                val err = profileResult.exceptionOrNull()
                Log.e(TAG, "finishOnboarding: [1] FAILED saving profile", err)
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage(err?.message ?: "Error al guardar perfil")
                return@launch
            }
            Log.d(TAG, "finishOnboarding: [1] Profile saved OK")

            // 2. Create service (menu + categories + dishes) locally
            Log.d(TAG, "finishOnboarding: [2] Creating service locally — name=${currentState.serviceName}")
            val serviceResult = createService(userId, currentState)
            if (serviceResult.isFailure) {
                val err = serviceResult.exceptionOrNull()
                Log.e(TAG, "finishOnboarding: [2] FAILED creating service", err)
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage(err?.message ?: "Error al crear servicio")
                return@launch
            }
            Log.d(TAG, "finishOnboarding: [2] Service created OK")

            // Cancel worker again — steps 1-2 re-enqueue it internally via scheduleUpload()
            syncManager.cancelUploadWorker(userId)
            Log.d(TAG, "finishOnboarding: UploadWorker cancelled (post local writes)")

            // 3. Push profile to remote (must go before menu — menu references profile)
            Log.d(TAG, "finishOnboarding: [3] Pushing profile to remote...")
            val profileSyncResult = profileRepository.syncUp(userId)
            if (profileSyncResult.isFailure) {
                val err = profileSyncResult.exceptionOrNull()
                Log.e(TAG, "finishOnboarding: [3] FAILED syncing profile to remote", err)
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage("Error al sincronizar perfil: ${err?.message}")
                return@launch
            }
            Log.d(TAG, "finishOnboarding: [3] Profile synced to remote OK")

            // 4. Push menu (and categories/dishes) to remote
            Log.d(TAG, "finishOnboarding: [4] Pushing service (menu) to remote...")
            val serviceSyncResult = syncService(userId)
            if (serviceSyncResult.isFailure) {
                val err = serviceSyncResult.exceptionOrNull()
                Log.e(TAG, "finishOnboarding: [4] FAILED syncing service to remote", err)
                _state.update { it.copy(isLoading = false) }
                snackbarManager.showMessage("Error al sincronizar servicio: ${err?.message}")
                return@launch
            }
            Log.d(TAG, "finishOnboarding: [4] Service synced to remote OK")

            // 5. Navigate to Home — replace backstack so the user can't go back to onboarding
            Log.d(TAG, "finishOnboarding: [5] All steps OK — navigating to Home")
            _state.update { it.copy(isLoading = false) }
            snackbarManager.showMessage("¡Bienvenido a Átomo!")
            rootNavigation.replaceWith(RouteApp.Home)
            Log.d(TAG, "finishOnboarding: [5] Navigation triggered")
        }
    }

    companion object {
        private const val TAG = "OnboardingVM"
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
