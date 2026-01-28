/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.shop.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun ShopShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar or Header
        ShimmerLine(modifier = Modifier.fillMaxWidth().height(50.dp))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Grid of products
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(6) {
                AtomoCard(
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Image
                        ShimmerLine(modifier = Modifier.fillMaxWidth().height(120.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        // Text
                        ShimmerLine(modifier = Modifier.width(100.dp).height(16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerLine(modifier = Modifier.width(60.dp).height(14.dp))
                    }
                }
            }
        }
    }
}
