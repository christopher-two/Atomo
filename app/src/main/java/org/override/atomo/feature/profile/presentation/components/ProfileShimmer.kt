/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun ProfileShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        // Avatar (matching 200.dp size from ProfileDetailView)
        // Using a Box to mimic the container padding
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            ShimmerCircle(modifier = Modifier.fillMaxSize())
        }

        // Info Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Display Name
            ShimmerLine(modifier = Modifier
                .height(32.dp)
                .width(240.dp))
            Spacer(modifier = Modifier.height(8.dp))
            // Username
            ShimmerLine(modifier = Modifier
                .height(20.dp)
                .width(160.dp))
            Spacer(modifier = Modifier.height(8.dp))
            // Joined Date
            ShimmerLine(modifier = Modifier
                .height(16.dp)
                .width(120.dp))
        }

        // Link Sharing Actions Row
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sync Button
            ShimmerCircle(modifier = Modifier.size(40.dp))
            // Share Button
            ShimmerCircle(modifier = Modifier.size(40.dp))
        }

        // Social Links Header
        ShimmerLine(
            modifier = Modifier
                .align(Alignment.Start)
                .width(100.dp)
                .height(24.dp)
        )

        // Social Links List Items
        repeat(3) {
            androidx.compose.material3.ListItem(
                headlineContent = { ShimmerLine(modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)) },
                supportingContent = {
                    ShimmerLine(
                        modifier = Modifier
                            .width(180.dp)
                            .height(14.dp)
                    )
                },
                colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                leadingContent = { ShimmerCircle(modifier = Modifier.size(40.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}
