/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.feature.qr.domain.model.QrLogoType
import org.override.atomo.feature.qr.domain.usecase.SaveQrUseCase

class QrViewModel(
    private val param: String,
    private val saveQrUseCase: SaveQrUseCase
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
            is QrAction.UpdateForegroundColor -> _state.update {
                val isLight = action.color.luminance() > 0.5f
                val backgroundColor = if (isLight) Color.Black else Color.White
                it.copy(
                    config = it.config.copy(
                        darkColor = action.color,
                        frameColor = action.color,
                        ballColor = action.color,
                        lightColor = backgroundColor
                    )
                )
            }
            is QrAction.UpdateLogoType -> _state.update {
                it.copy(config = it.config.copy(logoType = action.type))
            }
            is QrAction.SetCustomLogo -> _state.update {
                it.copy(config = it.config.copy(customLogoUri = action.uri, logoType = QrLogoType.Custom))
            }
            is QrAction.Download -> {
                viewModelScope.launch {
                    saveQrUseCase(action.bitmap, action.text)
                }
            }
        }
    }
}