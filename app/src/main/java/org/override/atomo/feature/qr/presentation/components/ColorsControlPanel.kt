/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.qr.domain.model.QrConfig
import org.override.atomo.feature.qr.presentation.QrAction

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
        LazyRow(
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
