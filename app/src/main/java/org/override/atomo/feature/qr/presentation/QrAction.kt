/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import androidx.compose.ui.graphics.Color
import android.graphics.Bitmap

sealed interface QrAction {
    data object Download : QrAction
    
    // Configuration Actions
    data class UpdatePixelShape(val shape: QrPixelShapeType) : QrAction
    data class UpdateFrameShape(val shape: QrFrameShapeType) : QrAction
    data class UpdateBallShape(val shape: QrBallShapeType) : QrAction
    
    data class UpdateDarkColor(val color: Color) : QrAction
    data class UpdateLightColor(val color: Color) : QrAction
    data class UpdateFrameColor(val color: Color) : QrAction
    data class UpdateBallColor(val color: Color) : QrAction
    
    data class UpdateLogoType(val type: QrLogoType) : QrAction
    data class SetCustomLogo(val uri: String) : QrAction
}