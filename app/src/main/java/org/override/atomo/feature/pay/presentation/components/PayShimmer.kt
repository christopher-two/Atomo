/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.pay.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun PayShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
    ) {
        repeat(2) {
            AtomoCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Star Icon (Premium)
                    ShimmerCircle(modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    // App Name
                    ShimmerLine(modifier = Modifier
                        .width(120.dp)
                        .height(32.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Price
                    ShimmerLine(modifier = Modifier
                        .width(100.dp)
                        .height(40.dp))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider
                    ShimmerLine(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Features list
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            16.dp
                        )
                    ) {
                        repeat(4) {
                            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                                ShimmerCircle(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                ShimmerLine(modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Button
                    ShimmerLine(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}
