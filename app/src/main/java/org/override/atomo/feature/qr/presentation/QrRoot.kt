/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.override.atomo.R
import org.override.atomo.feature.qr.domain.model.QrBallShapeType
import org.override.atomo.feature.qr.domain.model.QrFrameShapeType
import org.override.atomo.feature.qr.domain.model.QrLogoType
import org.override.atomo.feature.qr.domain.model.QrPixelShapeType
import org.override.atomo.feature.qr.presentation.components.ColorsControlPanel
import org.override.atomo.feature.qr.presentation.components.LogoControlPanel
import org.override.atomo.feature.qr.presentation.components.ShapesControlPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrRoot(
    viewModel: QrViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var currentPainter: androidx.compose.ui.graphics.painter.Painter? by remember {
        androidx.compose.runtime.mutableStateOf(
            null
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor de QR") },
                actions = {
                    IconButton(onClick = {
                        currentPainter?.let { painter ->
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    val width = 1080
                                    val height = 1350
                                    val bitmap =
                                        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                    val androidCanvas = android.graphics.Canvas(bitmap)
                                    androidCanvas.drawColor(android.graphics.Color.WHITE)

                                    val canvas = Canvas(androidCanvas)
                                    val qrSize = 800f
                                    val qrX = (width - qrSize) / 2f
                                    val qrY = 200f

                                    val drawScope = CanvasDrawScope()
                                    drawScope.draw(
                                        density = Density(context),
                                        layoutDirection = LayoutDirection.Ltr,
                                        canvas = canvas,
                                        size = Size(width.toFloat(), height.toFloat())
                                    ) {
                                        translate(left = qrX, top = qrY) {
                                            with(painter) {
                                                draw(Size(qrSize, qrSize))
                                            }
                                        }
                                    }

                                    val text = state.data
                                    if (!text.isNullOrBlank()) {
                                        val paint = android.graphics.Paint().apply {
                                            color = android.graphics.Color.parseColor("#3C1A06")
                                            textSize = 80f
                                            typeface = android.graphics.Typeface.create(
                                                android.graphics.Typeface.SANS_SERIF,
                                                android.graphics.Typeface.BOLD
                                            )
                                            textAlign = android.graphics.Paint.Align.CENTER
                                            isAntiAlias = true
                                            letterSpacing = 0.05f
                                        }
                                        val textY = qrY + qrSize + 160f
                                        val displayText =
                                            text.removePrefix("https://").removePrefix("http://")
                                                .uppercase()
                                        androidCanvas.drawText(
                                            displayText,
                                            width / 2f,
                                            textY,
                                            paint
                                        )
                                    }

                                    viewModel.onAction(QrAction.Download(bitmap, state.data))
                                }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Descargar")
                    }
                }
            )
        }
    ) { paddingValues ->
        QrEditorScreen(
            state = state,
            onAction = viewModel::onAction,
            onPainterUpdated = { currentPainter = it },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun QrEditorScreen(
    state: QrState,
    onAction: (QrAction) -> Unit,
    onPainterUpdated: (androidx.compose.ui.graphics.painter.Painter) -> Unit,
    modifier: Modifier = Modifier
) {
    val config = state.config

    // Mappers for shapes
    val pixelShape = when (config.pixelShape) {
        QrPixelShapeType.Square -> QrPixelShape.Default
        QrPixelShapeType.Circle -> QrPixelShape.circle()
        QrPixelShapeType.Round -> QrPixelShape.roundCorners()
    }

    val frameShape = when (config.frameShape) {
        QrFrameShapeType.Square -> QrFrameShape.Default
        QrFrameShapeType.Circle -> QrFrameShape.circle()
        QrFrameShapeType.Round -> QrFrameShape.roundCorners(.25f)
    }

    val ballShape = when (config.ballShape) {
        QrBallShapeType.Square -> QrBallShape.Default
        QrBallShapeType.Circle -> QrBallShape.circle()
        QrBallShapeType.Round -> QrBallShape.roundCorners(.25f)
    }

    // Logo Painter Logic
    val logoPainter = when (config.logoType) {
        QrLogoType.None -> null
        QrLogoType.Default -> painterResource(R.drawable.logo_atomo_app_monochrome)
        QrLogoType.Custom -> {
            if (config.customLogoUri != null) {
                coil3.compose.rememberAsyncImagePainter(
                    model = config.customLogoUri
                )
            } else {
                painterResource(R.drawable.logo_atomo_app_monochrome) // Fallback
            }
        }
    }

    val shapes = QrShapes(
        code = QrCodeShape.Default,
        darkPixel = pixelShape,
        lightPixel = pixelShape,
        ball = ballShape,
        frame = frameShape,
        centralSymmetry = true
    )

    val colors = QrColors(
        dark = QrBrush.brush { Brush.linearGradient(listOf(config.darkColor, config.darkColor)) },
        light = QrBrush.brush {
            Brush.linearGradient(
                listOf(
                    config.lightColor,
                    config.lightColor
                )
            )
        },
        ball = QrBrush.brush { Brush.linearGradient(listOf(config.ballColor, config.ballColor)) },
        frame = QrBrush.brush { Brush.linearGradient(listOf(config.frameColor, config.frameColor)) }
    )

    val qrPainter = if (logoPainter != null) {
        rememberQrCodePainter(
            data = state.data ?: "",
            errorCorrectionLevel = QrErrorCorrectionLevel.High,
            shapes = shapes,
            logo = QrLogo(
                painter = logoPainter,
                size = 0.2f,
                padding = QrLogoPadding.Accurate(.1f),
                shape = QrLogoShape.circle(),
            ),
            colors = colors
        )
    } else {
        rememberQrCodePainter(
            data = state.data ?: "",
            errorCorrectionLevel = QrErrorCorrectionLevel.High,
            shapes = shapes,
            colors = colors
        )
    }

    androidx.compose.runtime.LaunchedEffect(qrPainter) {
        onPainterUpdated(qrPainter)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Preview Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = qrPainter,
                    contentDescription = "QR Preview",
                    modifier = Modifier
                        .padding(32.dp)
                        .size(280.dp)
                )
            }
        }

        // Control Panel
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("Formas", "Colores", "Iconos")

        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            Icon(
                                when (index) {
                                    0 -> Icons.Default.Token
                                    1 -> Icons.Default.Palette
                                    else -> Icons.Default.Image // Need to import Image
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> ShapesControlPanel(state.config, onAction)
                    1 -> ColorsControlPanel(state.config, onAction)
                    2 -> LogoControlPanel(state.config, onAction)
                }
            }
        }
    }
}
