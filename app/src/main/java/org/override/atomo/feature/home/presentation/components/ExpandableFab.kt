/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.home.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import org.override.atomo.domain.model.ServiceType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandableFab(
    expanded: Boolean,
    availableServiceTypes: List<ServiceType>,
    onToggle: () -> Unit,
    onCreateService: (ServiceType) -> Unit
) {
    val containerColor = colorScheme.primaryContainer
    val contentColor = colorScheme.onPrimaryContainer

    val allItems = listOf(
        FabItem(ServiceType.DIGITAL_MENU, Icons.Default.Restaurant, "Digital Menu"),
        FabItem(ServiceType.PORTFOLIO, Icons.Default.Folder, "Portfolio"),
        FabItem(ServiceType.CV, Icons.Default.Badge, "CV"),
        FabItem(ServiceType.SHOP, Icons.Default.Storefront, "Shop"),
        FabItem(ServiceType.INVITATION, Icons.Default.CardGiftcard, "Invitation")
    )
    
    // Filter to only show available items (services that don't exist yet)
    val items = allItems.filter { it.type in availableServiceTypes }

    val fabRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fab_rotation"
    )
    
    // Don't show FAB if no services can be created
    if (items.isEmpty() && !expanded) {
        return
    }

    Box(
        modifier = Modifier
            .semantics {
                isTraversalGroup = true
                contentDescription = "Add new service menu"
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = if (expanded) "Close menu" else "Open menu",
                        action = { onToggle(); true }
                    )
                )
            }
    ) {
        FloatingActionButtonMenu(
            expanded = expanded,
            button = {
                ToggleFloatingActionButton(
                    modifier = Modifier.semantics { traversalIndex = -1f },
                    checked = expanded,
                    containerColor = { containerColor },
                    onCheckedChange = { onToggle() }
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Add
                        }
                    }
                    Icon(
                        imageVector = imageVector,
                        contentDescription = if (expanded) "Close" else "Add service",
                        modifier = Modifier.rotate(fabRotation),
                        tint = contentColor
                    )
                }
            }
        ) {
            if (items.isEmpty()) {
                // Show message when no services available
                FloatingActionButtonMenuItem(
                    modifier = Modifier.semantics { traversalIndex = 0f },
                    onClick = { onToggle() },
                    icon = {},
                    text = { Text(text = "Actualiza tu plan para crear más servicios") }
                )
            } else {
                items.forEachIndexed { index, item ->
                    FloatingActionButtonMenuItem(
                        modifier = Modifier.semantics {
                            traversalIndex = (items.size - index).toFloat()
                        },
                        onClick = { onCreateService(item.type) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        text = { Text(text = item.label) }
                    )
                }
            }
        }
    }
}

data class FabItem(
    val type: ServiceType,
    val icon: ImageVector,
    val label: String
)