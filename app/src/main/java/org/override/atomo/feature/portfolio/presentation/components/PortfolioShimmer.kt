/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.portfolio.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun PortfolioShimmer() {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        items(6) {
            AtomoCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                 Box(modifier = Modifier.padding(16.dp)) {
                     Column {
                         // Title
                         ShimmerLine(modifier = Modifier
                             .width(180.dp)
                             .height(24.dp))
                         Spacer(modifier = Modifier.height(8.dp))
                         // Status
                         ShimmerLine(modifier = Modifier
                             .width(60.dp)
                             .height(16.dp))

                         Spacer(modifier = Modifier.height(12.dp))
                         // Delete Icon placeholder
                         org.override.atomo.core.ui.components.ShimmerCircle(
                             modifier = Modifier.size(
                                 24.dp
                             )
                         )
                     }
                 }
            }
        }
    }
}
