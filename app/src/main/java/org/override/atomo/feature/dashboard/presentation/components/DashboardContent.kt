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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.DashboardState

/**
 * Contenido principal del dashboard: columna lazy con header, estadísticas,
 * atajos, publicidad y tarjetas de servicios.
 *
 * Stateless: recibe [state] inmutable y [onAction] como callback.
 */
@Composable
fun DashboardContent(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val isServices = !state.hasAnyServices && !state.isLoading
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (!isServices) {
            /* Header con saludo */
            item(key = "header") {
                DashboardHeader(
                    displayName = state.profile?.displayName?.trim()?.substringBefore(' '),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (!isServices) {
            /* Estadísticas */
            item(key = "stats") {
                DashboardStats(
                    statistics = state.statistics,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        /* Atajos rápidos */
        if (state.shortcuts.isNotEmpty() && !isServices) {
            item(key = "shortcuts") {
                DashboardShortcuts(
                    shortcuts = state.shortcuts,
                    onAction = onAction,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        /* Título de servicios */
        if (state.hasAnyServices && !isServices) {
            item(key = "services_title") {
                Text(
                    text = "Tus Servicios",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        /* Tarjetas de servicio dinámicas o Empty State */
        if (isServices) {
            item(key = "empty_state") {
                DashboardEmptyState(onAction = onAction)
            }
        } else {
            dashboardServiceItems(
                services = state.services,
                onAction = onAction
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

