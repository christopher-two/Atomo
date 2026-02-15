/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Token
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import org.override.atomo.R
import org.override.atomo.feature.qr.presentation.util.QrSaver.saveQrToGallery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrRoot(
    viewModel: QrViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // We need to access painter in order to save it. 
    // Best way is to hoist the painter creation or expose a callback request.
    // However, painter is created inside QrEditorScreen.
    // Let's pass a request to save action down or change architecture slightly.
    // Easiest: Handle "Download" intent in ViewModel, observe it here, consume it, and trigger save.
    // But we are in "QrRoot", let's keep it simple: 
    // We will hoist the painter creation to QrRoot or pass a callback "onPainterReady" 
    
    // Actually, `QrEditorScreen` can expose the painter or handle the save itself.
    // Let's pass the "save" trigger to QrEditorScreen? No.
    // Let's move standard Scaffold into QrEditorScreen for easier access? No.
    
    // Let's redefine QrEditorScreen to take a `onSave: (Painter) -> Unit`.
    // Valid.
    
    var currentPainter: androidx.compose.ui.graphics.painter.Painter? by remember { androidx.compose.runtime.mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor de QR") },
                actions = {
                    IconButton(onClick = { 
                        currentPainter?.let { painter ->
                            scope.launch {
                                saveQrToGallery(context, painter)
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
        light = QrBrush.brush { Brush.linearGradient(listOf(config.lightColor, config.lightColor)) },
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
                                when(index) {
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

@Composable
fun LogoControlPanel(config: QrConfig, onAction: (QrAction) -> Unit) {
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAction(QrAction.SetCustomLogo(it.toString())) }
    }

    Text("Tipo de Icono", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onAction(QrAction.UpdateLogoType(QrLogoType.None)) }) {
            RadioButton(selected = config.logoType == QrLogoType.None, onClick = { onAction(QrAction.UpdateLogoType(QrLogoType.None)) })
            Text("Ninguno")
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onAction(QrAction.UpdateLogoType(QrLogoType.Default)) }) {
            RadioButton(selected = config.logoType == QrLogoType.Default, onClick = { onAction(QrAction.UpdateLogoType(QrLogoType.Default)) })
            Text("Logo de la App")
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { 
             launcher.launch("image/*")
        }) {
            RadioButton(selected = config.logoType == QrLogoType.Custom, onClick = { launcher.launch("image/*") })
            Text("Subir Imagen")
        }
    }
}

@Composable
fun ShapesControlPanel(config: QrConfig, onAction: (QrAction) -> Unit) {
    Text("Píxeles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    ShapeSelector(
        current = config.pixelShape,
        onSelect = { onAction(QrAction.UpdatePixelShape(it)) }
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    Text("Marcos (Ojos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    ShapeSelector(
        current = config.frameShape,
        onSelect = { onAction(QrAction.UpdateFrameShape(it)) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text("Bolas (Ojos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    ShapeSelector(
        current = config.ballShape,
        onSelect = { onAction(QrAction.UpdateBallShape(it)) }
    )
}

@Composable
fun <T : Enum<T>> ShapeSelector(current: T, onSelect: (T) -> Unit) {
    val items = current::class.java.enumConstants ?: return
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
       items.forEach { shape ->
           Column(horizontalAlignment = Alignment.CenterHorizontally) {
               RadioButton(
                   selected = shape == current,
                   onClick = { onSelect(shape) }
               )
               Text(shape.name, style = MaterialTheme.typography.bodySmall)
           }
       }
    }
}

@Composable
fun ColorsControlPanel(config: QrConfig, onAction: (QrAction) -> Unit) {
    ColorPickerItem("Color de Datos", config.darkColor) { onAction(QrAction.UpdateDarkColor(it)) }
    Spacer(modifier = Modifier.height(12.dp))
    ColorPickerItem("Fondo", config.lightColor) { onAction(QrAction.UpdateLightColor(it)) }
    Spacer(modifier = Modifier.height(12.dp))
    ColorPickerItem("Color de Ojos (Marco)", config.frameColor) { onAction(QrAction.UpdateFrameColor(it)) }
    Spacer(modifier = Modifier.height(12.dp))
    ColorPickerItem("Color de Ojos (Centro)", config.ballColor) { onAction(QrAction.UpdateBallColor(it)) }
}

@Composable
fun ColorPickerItem(label: String, currentColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color.Black, Color.DarkGray, Color.Gray,
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4),
        Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50)
    )

    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Simple horizontal scroll row for colors
        androidx.compose.foundation.lazy.LazyRow(
             horizontalArrangement = Arrangement.spacedBy(8.dp),
             modifier = Modifier.fillMaxWidth()
        ) {
             items(colors.size) { index ->
                 val color = colors[index]
                 Box(
                     modifier = Modifier
                         .size(40.dp)
                         .clip(CircleShape)
                         .background(color)
                         .border(
                             width = if (color == currentColor) 3.dp else 1.dp,
                             color = if (color == currentColor) MaterialTheme.colorScheme.primary else Color.LightGray,
                             shape = CircleShape
                         )
                         .clickable { onColorSelected(color) }
                 )
             }
             
             item {
                 // White option separately to see border
                 Box(
                    modifier = Modifier
                         .size(40.dp)
                         .clip(CircleShape)
                         .background(Color.White)
                         .border(1.dp, Color.Gray, CircleShape)
                         .clickable { onColorSelected(Color.White) }
                 )
             }
         }
    }
}