/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.dashboard.domain.model.DashboardShortcut
import org.override.atomo.feature.dashboard.domain.model.DashboardStatistics
import org.override.atomo.feature.dashboard.presentation.components.DashboardContent
import org.override.atomo.feature.dashboard.presentation.components.DashboardShimmer
import org.override.atomo.feature.profile.domain.model.Profile

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is DashboardEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is DashboardEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                    context.startActivity(intent)
                }

                is DashboardEvent.ShareUrl -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${event.text}\n${event.url}")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                }
            }
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Box(modifier = Modifier.fillMaxSize()) {

        /* Overlay de carga inicial */
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            DashboardShimmer()
        }

        /* Indicador de operación en curso (esquina inferior derecha) */
        AnimatedVisibility(
            visible = state.isOperationLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            ContainedLoadingIndicator(modifier = Modifier.size(24.dp))
        }

        /* Contenido principal */
        AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(DashboardAction.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                DashboardContent(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    AtomoTheme {
        DashboardScreen(
            state = DashboardState(
                isLoading = false,
                profile = Profile(
                    id = "1",
                    username = "atomo_user",
                    displayName = "Christopher Maldonado",
                    avatarUrl = null,
                    socialLinks = emptyMap(),
                    createdAt = 0L,
                    updatedAt = 0L
                ),
                statistics = DashboardStatistics(
                    activeServices = 3,
                    totalViews = 1540,
                    totalInteractions = 89
                ),
                shortcuts = listOf(
                    DashboardShortcut(
                        id = "1",
                        title = "Nuevo Menú",
                        icon = Icons.Default.Add,
                        action = DashboardAction.CreateMenu
                    ),
                    DashboardShortcut(
                        id = "2",
                        title = "Mi QR",
                        icon = Icons.Default.QrCode,
                        action = DashboardAction.ShowQR(org.override.atomo.domain.model.ServiceType.CV, "1")
                    )
                )
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
