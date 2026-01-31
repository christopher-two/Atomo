/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Appearance Section (4 items)
        SettingsSectionShimmer(titleWidth = 100.dp, itemCount = 4)

        // Notifications Section (3 items)
        SettingsSectionShimmer(titleWidth = 110.dp, itemCount = 3)

        // Privacy Section (2 items)
        SettingsSectionShimmer(titleWidth = 80.dp, itemCount = 2)

        // Subscription Section
        Column {
            ShimmerLine(modifier = Modifier
                .width(100.dp)
                .height(20.dp))
            Spacer(modifier = Modifier.height(16.dp))
            ShimmerLine(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
            )
        }

        // Account Section
        Column {
            ShimmerLine(modifier = Modifier
                .width(80.dp)
                .height(20.dp))
            Spacer(modifier = Modifier.height(16.dp))
            ShimmerLine(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSectionShimmer(titleWidth: androidx.compose.ui.unit.Dp, itemCount: Int) {
    Column {
        ShimmerLine(modifier = Modifier
            .width(titleWidth)
            .height(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        AtomoCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(itemCount) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            ShimmerLine(modifier = Modifier
                                .width(150.dp)
                                .height(16.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerLine(modifier = Modifier
                                .width(200.dp)
                                .height(12.dp))
                        }
                        ShimmerLine(
                            modifier = Modifier
                                .width(40.dp)
                                .height(24.dp),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                    }
                    if (index < itemCount - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
