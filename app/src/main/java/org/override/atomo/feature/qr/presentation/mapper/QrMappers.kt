/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil3.compose.rememberAsyncImagePainter
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import org.override.atomo.R
import org.override.atomo.feature.qr.domain.model.QrBallShapeType
import org.override.atomo.feature.qr.domain.model.QrConfig
import org.override.atomo.feature.qr.domain.model.QrFrameShapeType
import org.override.atomo.feature.qr.domain.model.QrLogoType
import org.override.atomo.feature.qr.domain.model.QrPixelShapeType

/**
 * Extension methods to map Domain QrConfig properties 
 * cleanly to the Qrose UI model specifications.
 */

fun QrConfig.toQrShapes(): QrShapes {
    val pixelShape = when (this.pixelShape) {
        QrPixelShapeType.Square -> QrPixelShape.Default
        QrPixelShapeType.Circle -> QrPixelShape.circle()
        QrPixelShapeType.Round -> QrPixelShape.roundCorners()
    }

    val frameShape = when (this.frameShape) {
        QrFrameShapeType.Square -> QrFrameShape.Default
        QrFrameShapeType.Circle -> QrFrameShape.circle()
        QrFrameShapeType.Round -> QrFrameShape.roundCorners(.25f)
    }

    val ballShape = when (this.ballShape) {
        QrBallShapeType.Square -> QrBallShape.Default
        QrBallShapeType.Circle -> QrBallShape.circle()
        QrBallShapeType.Round -> QrBallShape.roundCorners(.25f)
    }

    return QrShapes(
        code = QrCodeShape.Default,
        darkPixel = pixelShape,
        lightPixel = pixelShape,
        ball = ballShape,
        frame = frameShape,
        centralSymmetry = true
    )
}

fun QrConfig.toQrColors(): QrColors {
    return QrColors(
        dark = QrBrush.brush { Brush.linearGradient(listOf(darkColor, darkColor)) },
        light = QrBrush.brush { Brush.linearGradient(listOf(lightColor, lightColor)) },
        ball = QrBrush.brush { Brush.linearGradient(listOf(ballColor, ballColor)) },
        frame = QrBrush.brush { Brush.linearGradient(listOf(frameColor, frameColor)) }
    )
}

@Composable
fun QrConfig.getLogoPainter(): Painter? {
    return when (logoType) {
        QrLogoType.None -> null
        QrLogoType.Default -> painterResource(R.drawable.logo_atomo_app_monochrome)
        QrLogoType.Custom -> {
            if (customLogoUri != null) {
                rememberAsyncImagePainter(model = customLogoUri)
            } else {
                painterResource(R.drawable.logo_atomo_app_monochrome)
            }
        }
    }
}
