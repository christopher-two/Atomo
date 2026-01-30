/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.override.atomo.R
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun QrRoot(
    viewModel: QrViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    QrScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun QrScreen(
    state: QrState,
    onAction: (QrAction) -> Unit,
) {
    val shape = QrShapes(
        code = QrCodeShape.Default,
        darkPixel = QrPixelShape.circle(),
        lightPixel = QrPixelShape.circle(),
        ball = QrBallShape.circle(),
        frame = QrFrameShape.circle(),
        centralSymmetry = true
    )
    val logo = QrLogo(
        painter = painterResource(R.drawable.logo_atomo_app_monochrome),//rememberVectorPainter(Icons.Default.Functions),
        size = 0.25f,
        padding = QrLogoPadding.Accurate(0.1f),
        shape = QrLogoShape.circle(),
    )
    val colorScheme = colorScheme
    val qrColors = QrColors(
        dark = QrBrush.brush {
            Brush.linearGradient(
                listOf(
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                )
            )
        },
        light = QrBrush.brush {
            Brush.linearGradient(
                listOf(
                    colorScheme.onBackground,
                    colorScheme.onBackground,
                    colorScheme.onBackground,
                )
            )
        },
        ball = QrBrush.brush {
            Brush.linearGradient(
                listOf(
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                )
            )
        },
        frame = QrBrush.brush {
            Brush.linearGradient(
                listOf(
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                    colorScheme.onPrimaryContainer,
                )
            )
        }
    )
    val qr = rememberQrCodePainter(
        data = state.data ?: "",
        errorCorrectionLevel = QrErrorCorrectionLevel.High,
        shapes = shape,
        logo = logo,
        colors = qrColors,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = {
            Surface(
                modifier = Modifier
                    .size(300.dp),
                color = colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = {
                        Image(
                            painter = qr,
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        QrScreen(
            state = QrState(),
            onAction = {}
        )
    }
}