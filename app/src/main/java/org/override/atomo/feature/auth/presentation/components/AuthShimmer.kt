/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
fun AuthShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo Placeholder
        ShimmerCircle(modifier = Modifier.size(208.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // App Name Placeholder
        ShimmerLine(modifier = Modifier
            .width(180.dp)
            .height(50.dp))

        Spacer(modifier = Modifier.weight(1f))

        // Continue with Google Button Placeholder
        ShimmerLine(
            modifier = Modifier
                .width(280.dp)
                .height(50.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp) // Button shape
        )

        Spacer(modifier = Modifier.weight(1f))

        // Terms and Conditions Placeholder
        ShimmerLine(modifier = Modifier
            .width(160.dp)
            .height(20.dp))
    }
}
