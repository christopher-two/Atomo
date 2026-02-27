/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QrViewModel(
    private val param: String
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(QrState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                _state.update { it.copy(data = param) }
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = QrState()
        )

    fun onAction(action: QrAction) {
        when (action) {
            is QrAction.UpdatePixelShape -> _state.update { 
                it.copy(config = it.config.copy(pixelShape = action.shape)) 
            }
            is QrAction.UpdateFrameShape -> _state.update { 
                it.copy(config = it.config.copy(frameShape = action.shape)) 
            }
            is QrAction.UpdateBallShape -> _state.update { 
                it.copy(config = it.config.copy(ballShape = action.shape)) 
            }
            is QrAction.UpdateDarkColor -> _state.update { 
                it.copy(config = it.config.copy(darkColor = action.color)) 
            }
            is QrAction.UpdateLightColor -> _state.update { 
                it.copy(config = it.config.copy(lightColor = action.color)) 
            }
            is QrAction.UpdateFrameColor -> _state.update { 
                it.copy(config = it.config.copy(frameColor = action.color)) 
            }
            is QrAction.UpdateBallColor -> _state.update { 
                it.copy(config = it.config.copy(ballColor = action.color)) 
            }
            is QrAction.UpdateLogoType -> _state.update {
                it.copy(config = it.config.copy(logoType = action.type))
            }
            is QrAction.SetCustomLogo -> _state.update {
                it.copy(config = it.config.copy(customLogoUri = action.uri, logoType = QrLogoType.Custom))
            }
            QrAction.Download -> {
                // To be implemented in UI layer or via Event
                // For now, we can just trigger an effect? 
                // Actually usually the ViewModel prepares data, but View controls the Bitmap capture.
            }
        }
    }
}