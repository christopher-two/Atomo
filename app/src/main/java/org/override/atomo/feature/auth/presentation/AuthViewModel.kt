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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.navigation.RootNavigation

sealed interface AuthEvent {
    data class ShowError(val message: String) : AuthEvent
    data object LoginSuccess : AuthEvent
    data class OpenUrl(val url: String) : AuthEvent
}

class AuthViewModel(
    private val continueWithGoogleUseCase: ContinueWithGoogleUseCase,
    private val rootNavigation: RootNavigation
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
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    rootNavigation.replaceWith(RouteApp.Home)
                    _events.send(AuthEvent.LoginSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _events.send(AuthEvent.ShowError(error.message ?: "Unknown error"))
                }
        }
    }
}