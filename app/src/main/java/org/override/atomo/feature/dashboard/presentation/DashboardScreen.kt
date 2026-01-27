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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.components.DashboardAd
import org.override.atomo.feature.dashboard.presentation.components.DashboardHeader
import org.override.atomo.feature.dashboard.presentation.components.DashboardShimmer
import org.override.atomo.feature.dashboard.presentation.components.DashboardShortcuts
import org.override.atomo.feature.dashboard.presentation.components.DashboardStats
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardDeleteDialog
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Loading overlay
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            DashboardShimmer()
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
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(DashboardAction.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp), // Extra padding for FAB/Snackbar
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header with greeting
                    item(key = "header") {
                        DashboardHeader(
                            displayName = state.profile?.displayName?.trim()?.substringBefore(' '),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Statistics
                    item(key = "stats") {
                        DashboardStats(
                            statistics = state.statistics,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Shortcuts
                    if (state.shortcuts.isNotEmpty()) {
                        item(key = "shortcuts") {
                            DashboardShortcuts(
                                shortcuts = state.shortcuts,
                                onAction = onAction,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    // Advertisement
                    item(key = "ad") {
                        DashboardAd(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Services Title
                    if (state.hasAnyServices) {
                        item(key = "services_title") {
                            androidx.compose.material3.Text(
                                text = "Tus Servicios",
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }
                    }

                    // Sections (active only)
                    state.services.filterIsInstance<ServiceModule.MenuModule>().firstOrNull()?.let {
                        if (it.isActive) menuSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.ShopModule>().firstOrNull()?.let {
                        if (it.isActive) shopSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.CvModule>().firstOrNull()?.let {
                        if (it.isActive) cvSection(it, onAction)
                    }

                    state.services.filterIsInstance<ServiceModule.PortfolioModule>().firstOrNull()
                        ?.let {
                            if (it.isActive) portfolioSection(it, onAction)
                        }

                    state.services.filterIsInstance<ServiceModule.InvitationModule>().firstOrNull()
                        ?.let {
                            if (it.isActive) invitationSection(it, onAction)
                        }
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
