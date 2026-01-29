/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuAction
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuState
import org.override.atomo.feature.digital_menu.presentation.components.DigitalMenuShimmer

@Composable
fun DigitalMenuListScreen(state: DigitalMenuState, onAction: (DigitalMenuAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(DigitalMenuAction.CreateMenu) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Menu")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.menus.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                DigitalMenuShimmer()
            }
        } else {
            if (state.menus.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(DigitalMenuAction.UpgradePlan) }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.menus) { menu ->
                        DigitalMenuItem(menu = menu, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalMenuItem(
    menu: Menu,
    onAction: (DigitalMenuAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(DigitalMenuAction.OpenMenu(menu.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = menu.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (menu.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
             IconButton(onClick = { onAction(DigitalMenuAction.DeleteMenu(menu.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Menu")
             }
        }
    }
}
