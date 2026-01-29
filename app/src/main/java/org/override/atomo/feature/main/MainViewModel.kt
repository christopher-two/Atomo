/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.usecase.session.CheckSessionUseCase
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.settings.domain.usecase.GetSettingsUseCase

class MainViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val rootNavigation: RootNavigation
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainState()
        )

    val settingsState = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    init {
        checkSession()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsState.collect { settings ->
                val appearance = settings?.appearance
                val seedColor = when (appearance?.theme) {
                    "pink" -> Color(0xFFFFB5E8)
                    "green" -> Color(0xFFB5FFD9)
                    "purple" -> Color(0xFFDDB5FF)
                    "blue" -> Color(0xFFB5DEFF)
                    else -> Color(0xFFDAEDFF)
                }
                
                val isDarkMode = if (appearance?.isSystemThemeEnabled == true) null else appearance?.isDarkModeEnabled
                
                _state.update { 
                    it.copy(
                        themeConfig = ThemeConfig(
                            isDarkMode = isDarkMode,
                            seedColor = seedColor,
                            useDynamicColors = appearance?.isDynamicColorEnabled ?: false
                        )
                    )
                }
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                // Wait for initial settings load to avoid theme flicker if possible, 
                // but prioritize auth check speed.
                
                val isLoggedIn = checkSessionUseCase().first()
                rootNavigation.setInitialRoute(isLoggedIn)
            } catch (e: Exception) {
                rootNavigation.setInitialRoute(false)
            } finally {
                _state.update { it.copy(isSessionChecked = true, isLoading = false) }
            }
        }
    }
}
