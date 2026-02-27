/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import org.override.atomo.feature.qr.presentation.components.ColorsControlPanel
import org.override.atomo.feature.qr.presentation.components.LogoControlPanel
import org.override.atomo.feature.qr.presentation.components.ShapesControlPanel
import org.override.atomo.feature.qr.presentation.mapper.getLogoPainter
import org.override.atomo.feature.qr.presentation.mapper.toQrColors
import org.override.atomo.feature.qr.presentation.mapper.toQrShapes
import org.override.atomo.feature.qr.presentation.util.generateQrBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrRoot(
    viewModel: QrViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var currentPainter: Painter? by remember {
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
                                val bitmap = generateQrBitmap(context, painter, state.data)
                                viewModel.onAction(QrAction.Download(bitmap, state.data))
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
    onPainterUpdated: (Painter) -> Unit,
    modifier: Modifier = Modifier
) {
    val config = state.config

    val shapes = config.toQrShapes()
    val colors = config.toQrColors()
    val logoPainter = config.getLogoPainter()

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

    LaunchedEffect(qrPainter) {
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
                                    else -> Icons.Default.Image
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
