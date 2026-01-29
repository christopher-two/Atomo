/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.auth.domain.usecase.LogoutUseCase
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.settings.domain.usecase.SettingsUseCases

import org.override.atomo.feature.navigation.RootNavigation

/**
 * ViewModel for managing Settings feature state and business logic.
 * Handles reading and updating app configuration and user preferences.
 */
class SettingsViewModel(
    private val useCases: SettingsUseCases,
    private val rootNavigation: RootNavigation,
    private val logoutUseCase: LogoutUseCase,
    private val homeNavigation: HomeNavigation
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            subscribeToSettings()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    /**
     * Processes user intents/actions.
     *
     * @param action The action to perform.
     */
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ToggleDarkMode -> updateTheme(action.enabled)
            is SettingsAction.SetTheme -> updateThemeSelection(action.theme)
            is SettingsAction.ToggleDynamicColor -> updateDynamicColor(action.enabled)
            is SettingsAction.ToggleSystemTheme -> updateSystemTheme(action.enabled)
            is SettingsAction.ToggleNotifications -> updateNotifications(action.enabled)
            is SettingsAction.ToggleNotificationSound -> updateNotificationSound(action.enabled)
            is SettingsAction.SetNotificationPriority -> updateNotificationPriority(action.priority)
            is SettingsAction.ToggleBiometricAuth -> updateBiometricAuth(action.enabled)
            is SettingsAction.ToggleAnalytics -> updateAnalytics(action.enabled)
            is SettingsAction.Logout -> logout()
            is SettingsAction.NavigateToPay -> homeNavigation.switchTab(AppTab.PAY)
            is SettingsAction.NavigateBack -> rootNavigation.back()
        }
    }

    private fun subscribeToSettings() {
        viewModelScope.launch {
            useCases.getSettings().collect { settings ->
                _state.update {
                    it.copy(
                        appearance = settings.appearance,
                        notifications = settings.notifications,
                        privacy = settings.privacy,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateTheme(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updateAppearance.toggleDarkMode(enabled)
        }
    }

    private fun updateThemeSelection(theme: String) {
        viewModelScope.launch {
            useCases.updateAppearance.setTheme(theme)
        }
    }

    private fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updateAppearance.toggleDynamicColor(enabled)
        }
    }

    private fun updateSystemTheme(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updateAppearance.toggleSystemTheme(enabled)
        }
    }

    private fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updateNotifications.toggleNotifications(enabled)
        }
    }

    private fun updateNotificationSound(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updateNotifications.toggleNotificationSound(enabled)
        }
    }

    private fun updateNotificationPriority(priority: Float) {
        viewModelScope.launch {
            useCases.updateNotifications.setNotificationPriority(priority)
        }
    }

    private fun updateBiometricAuth(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updatePrivacy.toggleBiometricAuth(enabled)
        }
    }

    private fun updateAnalytics(enabled: Boolean) {
        viewModelScope.launch {
            useCases.updatePrivacy.toggleAnalytics(enabled)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = logoutUseCase()
            _state.update { it.copy(isLoading = false) }
            
            if (result.isSuccess) {
                // Navegar a Auth y limpiar backstack
                rootNavigation.replaceWith(RouteApp.Auth)
            }
        }
    }
}