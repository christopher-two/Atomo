package org.override.atomo.feature.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.components.ServiceCard
import org.override.atomo.feature.dashboard.presentation.components.DashboardAd
import org.override.atomo.feature.dashboard.presentation.components.DashboardHeader
import org.override.atomo.feature.dashboard.presentation.components.DashboardShimmer
import org.override.atomo.feature.dashboard.presentation.components.DashboardShortcuts
import org.override.atomo.feature.dashboard.presentation.components.DashboardStats
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardDeleteDialog
import org.override.atomo.feature.dashboard.presentation.components.base.DashboardSheetsHandler

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
                            Text(
                                text = "Tus Servicios",
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }
                    }


                    // Render Service Cards dynamically
                    items(state.services) { module ->
                         if (module.isActive) {
                             when (module) {
                                 is ServiceModule.MenuModule -> {
                                     val menu = module.menus.first()
                                     ServiceCard(
                                         title = menu.name,
                                         subtitle = "${module.totalDishes} Platillos",
                                         icon = androidx.compose.material.icons.Icons.Filled.RestaurantMenu,
                                         onPreviewClick = { onAction(DashboardAction.PreviewService("menu", menu.id)) },
                                         onShareClick = { onAction(DashboardAction.ShareService("menu", menu.id)) },
                                         onQrClick = { onAction(DashboardAction.ShowQR("menu", menu.id)) },
                                         modifier = Modifier.padding(horizontal = 16.dp)
                                     )
                                 }
                                 
                                 is ServiceModule.ShopModule -> {
                                     val shop = module.shops.first()
                                      ServiceCard(
                                         title = shop.name,
                                         subtitle = "${module.totalProducts} Productos",
                                         icon = androidx.compose.material.icons.Icons.Filled.ShoppingBag,
                                         onPreviewClick = { onAction(DashboardAction.PreviewService("shop", shop.id)) },
                                         onShareClick = { onAction(DashboardAction.ShareService("shop", shop.id)) },
                                         onQrClick = { onAction(DashboardAction.ShowQR("shop", shop.id)) },
                                         modifier = Modifier.padding(horizontal = 16.dp)
                                     )
                                 }
                                 
                                 is ServiceModule.CvModule -> {
                                     val cv = module.cvs.first()
                                      ServiceCard(
                                         title = cv.title,
                                         subtitle = "${module.totalSkills} Habilidades, ${module.totalExperiences} Exp.",
                                         icon = androidx.compose.material.icons.Icons.Filled.Description,
                                         onPreviewClick = { onAction(DashboardAction.PreviewService("cv", cv.id)) },
                                         onShareClick = { onAction(DashboardAction.ShareService("cv", cv.id)) },
                                         onQrClick = { onAction(DashboardAction.ShowQR("cv", cv.id)) },
                                         modifier = Modifier.padding(horizontal = 16.dp)
                                     )
                                 }
                                 
                                 is ServiceModule.PortfolioModule -> {
                                     val portfolio = module.portfolios.first()
                                      ServiceCard(
                                         title = portfolio.title,
                                         subtitle = "${module.totalItems} Proyectos",
                                         icon = androidx.compose.material.icons.Icons.Filled.Description,
                                         onPreviewClick = { onAction(DashboardAction.PreviewService("portfolio", portfolio.id)) },
                                         onShareClick = { onAction(DashboardAction.ShareService("portfolio", portfolio.id)) },
                                         onQrClick = { onAction(DashboardAction.ShowQR("portfolio", portfolio.id)) },
                                         modifier = Modifier.padding(horizontal = 16.dp)
                                     )
                                 }
                                 
                                 is ServiceModule.InvitationModule -> {
                                     val invitation = module.invitations.first()
                                     ServiceCard(
                                         title = invitation.eventName,
                                         subtitle = invitation.description ?: "Sin descripción",
                                         icon = androidx.compose.material.icons.Icons.Filled.Description,
                                         onPreviewClick = { onAction(DashboardAction.PreviewService("invitation", invitation.id)) },
                                         onShareClick = { onAction(DashboardAction.ShareService("invitation", invitation.id)) },
                                         onQrClick = { onAction(DashboardAction.ShowQR("invitation", invitation.id)) },
                                         modifier = Modifier.padding(horizontal = 16.dp)
                                     )
                                 }
                             }
                         }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
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
