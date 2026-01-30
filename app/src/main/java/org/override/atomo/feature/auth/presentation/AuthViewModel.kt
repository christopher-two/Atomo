/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.domain.usecase.onboarding.ShouldShowOnboardingUseCase
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.auth.domain.usecase.SaveUserSessionUseCase
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.auth.api.ExternalAuthResult

class AuthViewModel(
    private val continueWithGoogleUseCase: ContinueWithGoogleUseCase,
    private val saveUserSessionUseCase: SaveUserSessionUseCase,
    private val shouldShowOnboardingUseCase: ShouldShowOnboardingUseCase,
    private val rootNavigation: RootNavigation,
    private val syncManager: org.override.atomo.data.manager.SyncManager
) : ViewModel() {


    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(AuthState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        if (!hasLoadedInitialData) {
            /** Load initial data here **/
            hasLoadedInitialData = true
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.ContinueWithGoogle -> continueWithGoogle(action.context)
            is AuthAction.OpenUrl -> {
                viewModelScope.launch {
                    _events.send(AuthEvent.OpenUrl(action.url))
                }
            }
        }
    }

    private fun continueWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            continueWithGoogleUseCase(context)
                .onSuccess { result ->
                    when (result) {
                        is ExternalAuthResult.Success -> {
                            saveUserSessionUseCase(result.userId)
                                .onSuccess {
                                    _state.update { it.copy(isLoading = false) }
                                    syncManager.scheduleInitialSync(result.userId)
                                    navigateAfterLogin(result.userId)
                                    _events.send(AuthEvent.LoginSuccess)
                                }
                                .onFailure { error ->
                                    _state.update { it.copy(isLoading = false, error = error.message) }
                                    _events.send(AuthEvent.ShowError(error.message ?: "Failed to save session"))
                                }
                        }
                        is ExternalAuthResult.Error -> {
                            _state.update { it.copy(isLoading = false, error = result.message) }
                            _events.send(AuthEvent.ShowError(result.message))
                        }
                        ExternalAuthResult.Cancelled -> {
                            _state.update { it.copy(isLoading = false) }
                        }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _events.send(AuthEvent.ShowError(error.message ?: "Unknown error"))
                }
        }
    }

    private suspend fun navigateAfterLogin(userId: String) {
        _state.update { it.copy(isLoading = false, isSettingUp = true) }
        val shouldShowOnboarding = shouldShowOnboardingUseCase(userId).firstOrNull() ?: false
        if (shouldShowOnboarding) {
            rootNavigation.replaceWith(RouteApp.Onboarding)
        } else {
            rootNavigation.replaceWith(RouteApp.Home)
        }
    }
}