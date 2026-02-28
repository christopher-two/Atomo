/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.qr.domain.model.QrConfig
import org.override.atomo.feature.qr.presentation.QrAction

@Composable
fun ShapesControlPanel(config: QrConfig, onAction: (QrAction) -> Unit) {
    Text("Píxeles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    ShapeSelector(
        current = config.pixelShape,
        onSelect = { onAction(QrAction.UpdatePixelShape(it)) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        "Marcos (Ojos)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Enum<T>> ShapeSelector(current: T, onSelect: (T) -> Unit) {
    val items = current::class.java.enumConstants ?: return

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        ButtonGroup(
            overflowIndicator = {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { shape ->
                toggleableItem(
                    checked = shape == current,
                    label = shape.name,
                    weight = 2f,
                    onCheckedChange = {
                        onSelect(shape)
                    }
                )
            }
        }
    }
}
