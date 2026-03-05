/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerItem
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun DashboardShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column {
                ShimmerLine(modifier = Modifier
                    .width(180.dp)
                    .height(28.dp))
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerLine(modifier = Modifier
                    .width(120.dp)
                    .height(16.dp))
            }
        }

        // Stats
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) {
                    AtomoCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ShimmerLine(modifier = Modifier
                                .width(30.dp)
                                .height(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerLine(modifier = Modifier
                                .width(50.dp)
                                .height(12.dp))
                        }
                    }
                }
            }
        }

        // Shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    ShimmerItem(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Ad
        item {
            ShimmerItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Services Title
        item {
            ShimmerLine(modifier = Modifier
                .width(120.dp)
                .height(20.dp))
        }

        // Service Cards
        items(4) {
            AtomoCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerCircle(modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            ShimmerLine(modifier = Modifier
                                .width(150.dp)
                                .height(18.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerLine(modifier = Modifier
                                .width(100.dp)
                                .height(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Actions Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(3) {
                            ShimmerItem(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                   }
               }
            }
        }
    }
}
