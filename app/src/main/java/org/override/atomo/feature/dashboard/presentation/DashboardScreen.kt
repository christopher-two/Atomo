package org.override.atomo.feature.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.components.DashboardHeader
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardDeleteDialog
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardEmptyState
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardSheetsHandler
import org.override.atomo.feature.dashboard.presentation.components.sections.cvSection
import org.override.atomo.feature.dashboard.presentation.components.sections.invitationSection
import org.override.atomo.feature.dashboard.presentation.components.sections.menuSection
import org.override.atomo.feature.dashboard.presentation.components.sections.portfolioSection
import org.override.atomo.feature.dashboard.presentation.components.sections.shopSection

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = padding.calculateLeftPadding(LocalLayoutDirection.current),
                )
        ) {
            // Loading overlay
            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                ContainedLoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Operations loading overlay (small indicator)
            AnimatedVisibility(
                visible = state.isOperationLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                ContainedLoadingIndicator(
                    modifier = Modifier.size(24.dp),
                )
            }

            // Main content
            AnimatedVisibility(
                visible = !state.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp), // Extra padding for FAB/Snackbar
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with greeting
                    item(key = "header") {
                        DashboardHeader(
                            displayName = state.profile?.displayName,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Empty state when no services
                    if (!state.hasAnyServices && !state.isLoading) {
                        item(key = "empty") {
                            DashboardEmptyState(
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    // Sections
                    state.services.filterIsInstance<ServiceModule.MenuModule>().firstOrNull()?.let {
                        menuSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.PortfolioModule>().firstOrNull()
                        ?.let {
                            portfolioSection(it, onAction)
                        }

                    state.services.filterIsInstance<ServiceModule.CvModule>().firstOrNull()?.let {
                        cvSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.ShopModule>().firstOrNull()?.let {
                        shopSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.InvitationModule>().firstOrNull()
                        ?.let {
                            invitationSection(it, onAction)
                        }
                }
            }

            // Dialogs
            state.deleteDialog?.let { dialogState ->
                DashboardDeleteDialog(
                    dialogState = dialogState,
                    onAction = onAction
                )
            }

            // Sheets
            DashboardSheetsHandler(
                state = state,
                onAction = onAction
            )
        }
    }
}
