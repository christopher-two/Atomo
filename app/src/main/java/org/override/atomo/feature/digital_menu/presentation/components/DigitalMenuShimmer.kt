/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerItem
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun DigitalMenuShimmer() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sections / Categories Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) {
                    ShimmerItem(
                        modifier = Modifier
                            .width(100.dp)
                            .height(32.dp),
                        shape = androidx.compose.foundation.shape.CircleShape // Chip shape
                    )
                }
            }
        }

        // Menu Items
        items(6) {
            AtomoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                 Row(
                     modifier = Modifier
                         .padding(12.dp)
                         .fillMaxWidth(),
                     verticalAlignment = Alignment.Top
                 ) {
                     // Thumbnail (Rounded Rect 64.dp)
                     ShimmerItem(
                         modifier = Modifier.size(64.dp),
                         shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                     )

                     Spacer(modifier = Modifier.width(12.dp))

                     Column(modifier = Modifier.weight(1f)) {
                         // Title and Price Row
                         Row(
                             modifier = Modifier.fillMaxWidth(),
                             horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                         ) {
                             ShimmerLine(modifier = Modifier
                                 .width(120.dp)
                                 .height(18.dp))
                             Spacer(modifier = Modifier.width(8.dp))
                             ShimmerLine(modifier = Modifier
                                 .width(50.dp)
                                 .height(18.dp))
                         }
                         
                         Spacer(modifier = Modifier.height(8.dp))

                         // Description Lines
                         ShimmerLine(modifier = Modifier
                             .fillMaxWidth(0.9f)
                             .height(14.dp))
                         Spacer(modifier = Modifier.height(4.dp))
                         ShimmerLine(modifier = Modifier
                             .fillMaxWidth(0.6f)
                             .height(14.dp))
                     }
                 }
            }
        }
    }
}
