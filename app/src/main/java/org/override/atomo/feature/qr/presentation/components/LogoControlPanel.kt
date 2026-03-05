/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.components

import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.qr.domain.model.QrConfig
import org.override.atomo.feature.qr.domain.model.QrLogoType
import org.override.atomo.feature.qr.presentation.QrAction

@Composable
fun LogoControlPanel(config: QrConfig, onAction: (QrAction) -> Unit) {
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
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
