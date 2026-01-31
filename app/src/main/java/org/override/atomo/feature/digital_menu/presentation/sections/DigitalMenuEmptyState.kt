/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuAction
import org.override.atomo.feature.digital_menu.presentation.components.DigitalMenuShimmer

@Composable
fun DigitalMenuEmptyState(
    isLoading: Boolean,
    canCreate: Boolean,
    limitReached: Boolean,
    onAction: (DigitalMenuAction) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            DigitalMenuShimmer()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You don't have a menu yet.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (canCreate && !limitReached) {
                    Button(onClick = { onAction(DigitalMenuAction.CreateMenu) }) {
                        Text("Create Digital Menu")
                    }
                } else if (limitReached) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(DigitalMenuAction.UpgradePlan) }
                    )
                }
            }
        }
    }
}
