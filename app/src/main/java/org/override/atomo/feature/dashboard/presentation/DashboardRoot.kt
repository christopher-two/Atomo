package org.override.atomo.feature.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.dashboard.presentation.components.DashboardHeader
import org.override.atomo.feature.dashboard.presentation.components.DishPreviewRow
import org.override.atomo.feature.dashboard.presentation.components.PortfolioPreviewRow
import org.override.atomo.feature.dashboard.presentation.components.ProductPreviewRow
import org.override.atomo.feature.dashboard.presentation.components.ServiceModuleCard

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Loading overlay
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }
        
        // Main content
        AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with greeting
                item(key = "header") {
                    DashboardHeader(
                        displayName = state.profile?.displayName,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Empty state
                if (state.services.isEmpty() && !state.isLoading) {
                    item(key = "empty") {
                        EmptyServicesPlaceholder(
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                
                // Service modules
                items(
                    items = state.services,
                    key = { service -> service::class.simpleName ?: "" }
                ) { service ->
                    ServiceModuleItem(
                        service = service,
                        onAction = onAction,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ServiceModuleItem(
    service: ServiceModule,
    onAction: (DashboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (service) {
        is ServiceModule.MenuModule -> {
            // Only show if there are menus
            if (service.menus.isEmpty()) return
            
            val subtitle = when {
                service.menus.size == 1 -> "${service.totalDishes} platillos"
                else -> "${service.menus.size} menús · ${service.totalDishes} platillos"
            }
            
            ServiceModuleCard(
                icon = Icons.Outlined.Restaurant,
                title = "Menú Digital",
                subtitle = subtitle,
                accentColor = Color(0xFFE65100),
                onClick = {
                    service.menus.firstOrNull()?.let { 
                        onAction(DashboardAction.NavigateToMenu(it.id))
                    }
                },
                modifier = modifier,
                previewContent = if (service.recentDishes.isNotEmpty()) {
                    {
                        DishPreviewRow(
                            dishes = service.recentDishes,
                            onDishClick = { menuId ->
                                onAction(DashboardAction.NavigateToMenu(menuId))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else null
            )
        }
        
        is ServiceModule.PortfolioModule -> {
            // Only show if there are portfolios
            if (service.portfolios.isEmpty()) return
            
            val subtitle = when {
                service.portfolios.size == 1 -> "${service.totalItems} proyectos"
                else -> "${service.portfolios.size} portfolios · ${service.totalItems} proyectos"
            }
            
            ServiceModuleCard(
                icon = Icons.Outlined.PhotoLibrary,
                title = "Portfolio",
                subtitle = subtitle,
                accentColor = Color(0xFF7B1FA2),
                onClick = {
                    service.portfolios.firstOrNull()?.let {
                        onAction(DashboardAction.NavigateToPortfolio(it.id))
                    }
                },
                modifier = modifier,
                previewContent = if (service.recentItems.isNotEmpty()) {
                    {
                        PortfolioPreviewRow(
                            items = service.recentItems,
                            onItemClick = { portfolioId ->
                                onAction(DashboardAction.NavigateToPortfolio(portfolioId))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else null
            )
        }
        
        is ServiceModule.CvModule -> {
            // Only show if there are CVs
            if (service.cvs.isEmpty()) return
            
            val subtitle = when {
                service.cvs.size == 1 -> "${service.totalExperiences} experiencias · ${service.totalSkills} habilidades"
                else -> "${service.cvs.size} CVs"
            }
            
            ServiceModuleCard(
                icon = Icons.Outlined.Description,
                title = "Currículum",
                subtitle = subtitle,
                accentColor = Color(0xFF0288D1),
                onClick = {
                    service.cvs.firstOrNull()?.let {
                        onAction(DashboardAction.NavigateToCv(it.id))
                    }
                },
                modifier = modifier
            )
        }
        
        is ServiceModule.ShopModule -> {
            // Only show if there are shops
            if (service.shops.isEmpty()) return
            
            val subtitle = when {
                service.shops.size == 1 -> "${service.totalProducts} productos"
                else -> "${service.shops.size} tiendas · ${service.totalProducts} productos"
            }
            
            ServiceModuleCard(
                icon = Icons.Outlined.Inventory2,
                title = "Tienda",
                subtitle = subtitle,
                accentColor = Color(0xFF388E3C),
                onClick = {
                    service.shops.firstOrNull()?.let {
                        onAction(DashboardAction.NavigateToShop(it.id))
                    }
                },
                modifier = modifier,
                previewContent = if (service.recentProducts.isNotEmpty()) {
                    {
                        ProductPreviewRow(
                            products = service.recentProducts,
                            onProductClick = { shopId ->
                                onAction(DashboardAction.NavigateToShop(shopId))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else null
            )
        }
        
        is ServiceModule.InvitationModule -> {
            // Only show if there are invitations
            if (service.invitations.isEmpty()) return
            
            val subtitle = when {
                service.upcomingEvent != null -> "Próximo: ${service.upcomingEvent.eventName}"
                else -> "${service.activeCount} activas"
            }
            
            ServiceModuleCard(
                icon = Icons.Outlined.Mail,
                title = "Invitaciones",
                subtitle = subtitle,
                accentColor = Color(0xFFC2185B),
                onClick = {
                    service.invitations.firstOrNull()?.let {
                        onAction(DashboardAction.NavigateToInvitation(it.id))
                    }
                },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyServicesPlaceholder(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Text(
            text = "Crea tu primer servicio",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        DashboardScreen(
            state = DashboardState(isLoading = false),
            onAction = {}
        )
    }
}