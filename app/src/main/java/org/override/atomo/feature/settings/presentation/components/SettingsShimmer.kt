/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun SettingsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section 1
        ShimmerLine(modifier = Modifier.width(100.dp).height(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        AtomoCard(
            modifier = Modifier.fillMaxWidth().height(200.dp) // Appearance section size approx
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    repeat(4) {
                       Row(verticalAlignment = Alignment.CenterVertically) {
                           ShimmerLine(modifier = Modifier.weight(1f).height(16.dp))
                           Spacer(modifier = Modifier.width(16.dp))
                           ShimmerLine(modifier = Modifier.width(40.dp).height(20.dp))
                       }
                       Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Section 2
        ShimmerLine(modifier = Modifier.width(100.dp).height(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        AtomoCard(
            modifier = Modifier.fillMaxWidth().height(150.dp) // Notification section size approx
        ) {
             Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    repeat(3) {
                       Row(verticalAlignment = Alignment.CenterVertically) {
                           ShimmerLine(modifier = Modifier.weight(1f).height(16.dp))
                           Spacer(modifier = Modifier.width(16.dp))
                           ShimmerLine(modifier = Modifier.width(40.dp).height(20.dp))
                       }
                       Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
