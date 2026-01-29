/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.pay.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.feature.pay.presentation.components.PayShimmer
import org.override.atomo.feature.pay.presentation.components.PlanCard

@Composable
fun PayRoot(
    viewModel: PayViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PayScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PayScreen(
    state: PayState,
    onAction: (PayAction) -> Unit,
) {
    if (state.isLoading) {
        PayShimmer()
    } else {
        val uriHandler = LocalUriHandler.current

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 100.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.plans) { plan ->
                val isCurrent = plan.id == state.currentPlan?.id
                PlanCard(
                    plan = plan,
                    isCurrent = isCurrent,
                    onSubscribe = { onAction(PayAction.SelectPlan(plan)) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Términos y Condiciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://atomo.click/atomo/terms")
                        }
                    )

                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Privacidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://atomo.click/atomo/privacy")
                        }
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    if (state.showConfirmDialog && state.selectedPlan != null) {
        AlertDialog(
            containerColor = colorScheme.surface,
            onDismissRequest = { onAction(PayAction.DismissDialog) },
            title = { Text("Confirmar Suscripción") },
            text = {
                Text("¿Deseas cambiar al plan ${state.selectedPlan.name} por $${state.selectedPlan.price.toInt()} MXN/${state.selectedPlan.interval}?")
            },
            confirmButton = {
                Button(onClick = { onAction(PayAction.ConfirmSubscription) }) {
                    Text("Confirmar Compra")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(PayAction.DismissDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
