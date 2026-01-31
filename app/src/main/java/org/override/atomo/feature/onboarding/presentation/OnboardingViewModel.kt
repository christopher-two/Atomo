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
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.profile.domain.ProfileValidator
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

/**
 * ViewModel for the Onboarding feature.
 * Manages the wizard flow for completing user profile and creating first service.
 */
class OnboardingViewModel(
    private val profileUseCases: ProfileUseCases,
    private val shopUseCases: ShopUseCases,
    private val menuUseCases: MenuUseCases,
    private val portfolioUseCases: PortfolioUseCases,
    private val cvUseCases: CvUseCases,
    private val invitationUseCases: InvitationUseCases,
    // Repositories for direct sync
    private val profileRepository: org.override.atomo.domain.repository.ProfileRepository,
    private val menuRepository: org.override.atomo.domain.repository.MenuRepository,
    private val shopRepository: org.override.atomo.domain.repository.ShopRepository,
    private val portfolioRepository: org.override.atomo.domain.repository.PortfolioRepository,
    private val cvRepository: org.override.atomo.domain.repository.CvRepository,
    private val invitationRepository: org.override.atomo.domain.repository.InvitationRepository,
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
            is OnboardingAction.SelectServiceType -> {
                _state.update { it.copy(selectedServiceType = action.type) }
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
            OnboardingStep.PROFILE -> OnboardingStep.SERVICE
            OnboardingStep.SERVICE -> OnboardingStep.REVIEW
            OnboardingStep.REVIEW -> return // Already at last step
        }
        _state.update { it.copy(step = nextStep) }
    }

    private fun previousStep() {
        val currentStep = state.value.step
        val prevStep = when (currentStep) {
            OnboardingStep.PROFILE -> return // Already at first step
            OnboardingStep.SERVICE -> OnboardingStep.PROFILE
            OnboardingStep.REVIEW -> OnboardingStep.SERVICE
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
            val serviceSyncResult = syncService(userId, currentState.selectedServiceType)
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

    private suspend fun syncService(userId: String, serviceType: ServiceType?): Result<Unit> {
        return when (serviceType) {
            ServiceType.SHOP -> shopRepository.syncUp(userId)
            ServiceType.DIGITAL_MENU -> menuRepository.syncUp(userId)
            ServiceType.PORTFOLIO -> portfolioRepository.syncUp(userId)
            ServiceType.CV -> cvRepository.syncUp(userId)
            ServiceType.INVITATION -> invitationRepository.syncUp(userId)
            null -> Result.failure(Exception("No service type selected"))
        }
    }

    private suspend fun createService(userId: String, state: OnboardingState): Result<Unit> {
        val serviceType = state.selectedServiceType
            ?: return Result.failure(Exception("No service type selected"))
        val serviceName = state.serviceName
        val now = System.currentTimeMillis()

        // Default theme values
        val defaultPrimaryColor = "#6200EE"
        val defaultFontFamily = "Poppins"
        val defaultTemplateId = "default"

        return when (serviceType) {
            ServiceType.SHOP -> {
                shopUseCases.createShop(
                    Shop(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        name = serviceName,
                        description = null,
                        isActive = true,
                        primaryColor = defaultPrimaryColor,
                        fontFamily = defaultFontFamily,
                        createdAt = now
                    )
                ).map { }
            }

            ServiceType.DIGITAL_MENU -> {
                menuUseCases.createMenu(
                    Menu(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        name = serviceName,
                        description = null,
                        isActive = true,
                        templateId = defaultTemplateId,
                        primaryColor = defaultPrimaryColor,
                        fontFamily = defaultFontFamily,
                        logoUrl = null,
                        createdAt = now
                    )
                ).map { }
            }

            ServiceType.PORTFOLIO -> {
                portfolioUseCases.createPortfolio(
                    Portfolio(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = serviceName,
                        description = null,
                        isVisible = true,
                        templateId = defaultTemplateId,
                        primaryColor = defaultPrimaryColor,
                        fontFamily = defaultFontFamily,
                        createdAt = now
                    )
                ).map { }
            }

            ServiceType.CV -> {
                cvUseCases.createCv(
                    Cv(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = serviceName,
                        professionalSummary = null,
                        isVisible = true,
                        templateId = defaultTemplateId,
                        primaryColor = defaultPrimaryColor,
                        fontFamily = defaultFontFamily,
                        createdAt = now
                    )
                ).map { }
            }

            ServiceType.INVITATION -> {
                invitationUseCases.createInvitation(
                    Invitation(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        eventName = serviceName,
                        eventDate = null,
                        location = null,
                        description = null,
                        isActive = true,
                        templateId = defaultTemplateId,
                        primaryColor = defaultPrimaryColor,
                        fontFamily = defaultFontFamily,
                        createdAt = now
                    )
                ).map { }
            }
        }
    }
}
