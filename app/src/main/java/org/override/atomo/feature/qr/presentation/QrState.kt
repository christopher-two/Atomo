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

data class QrState(
    val data: String? = null,
    val config: QrConfig = QrConfig()
)

data class QrConfig(
    val pixelShape: QrPixelShapeType = QrPixelShapeType.Circle,
    val frameShape: QrFrameShapeType = QrFrameShapeType.Circle,
    val ballShape: QrBallShapeType = QrBallShapeType.Circle,
    val darkColor: Color = Color.Black,
    val lightColor: Color = Color.White,
    val frameColor: Color = Color.Black,
    val ballColor: Color = Color.Black,
    val logoType: QrLogoType = QrLogoType.Default,
    val customLogoUri: String? = null
)

enum class QrLogoType {
    None, Default, Custom
}

enum class QrPixelShapeType {
    Square, Circle, Round
}

enum class QrFrameShapeType {
    Square, Circle, Round
}

enum class QrBallShapeType {
    Square, Circle, Round
}