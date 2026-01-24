package org.override.atomo.feature.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ContainedLoadingIndicator
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
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard

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
            ContainedLoadingIndicator(
                modifier = Modifier.align(Alignment.Center)
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
                contentPadding = PaddingValues(bottom = 32.dp),
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
                        EmptyServicesPlaceholder(
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                
                // Menus section
                state.services.filterIsInstance<ServiceModule.MenuModule>().firstOrNull()?.let { menuModule ->
                    if (menuModule.menus.isNotEmpty()) {
                        item(key = "menus_header") {
                            SectionHeader(
                                title = "Menús Digitales",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = menuModule.menus,
                            key = { "menu_${it.id}" }
                        ) { menu ->
                            ServiceItemCard(
                                icon = Icons.Outlined.Restaurant,
                                title = menu.name,
                                subtitle = "${menu.dishes.size} platillos · ${menu.categories.size} categorías",
                                statusText = if (menu.isActive) "Activo" else "Inactivo",
                                accentColor = Color(0xFFE65100),
                                onEdit = { onAction(DashboardAction.EditMenu(menu.id)) },
                                onDelete = { onAction(DashboardAction.ConfirmDeleteMenu(menu)) },
                                onShare = { onAction(DashboardAction.ShareMenu(menu.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                
                // Portfolios section
                state.services.filterIsInstance<ServiceModule.PortfolioModule>().firstOrNull()?.let { portfolioModule ->
                    if (portfolioModule.portfolios.isNotEmpty()) {
                        item(key = "portfolios_header") {
                            SectionHeader(
                                title = "Portfolios",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = portfolioModule.portfolios,
                            key = { "portfolio_${it.id}" }
                        ) { portfolio ->
                            ServiceItemCard(
                                icon = Icons.Outlined.PhotoLibrary,
                                title = portfolio.title,
                                subtitle = "${portfolio.items.size} proyectos",
                                statusText = if (portfolio.isVisible) "Visible" else "Oculto",
                                accentColor = Color(0xFF7B1FA2),
                                onEdit = { onAction(DashboardAction.EditPortfolio(portfolio.id)) },
                                onDelete = { onAction(DashboardAction.ConfirmDeletePortfolio(portfolio)) },
                                onShare = { onAction(DashboardAction.SharePortfolio(portfolio.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                
                // CVs section
                state.services.filterIsInstance<ServiceModule.CvModule>().firstOrNull()?.let { cvModule ->
                    if (cvModule.cvs.isNotEmpty()) {
                        item(key = "cvs_header") {
                            SectionHeader(
                                title = "Currículums",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = cvModule.cvs,
                            key = { "cv_${it.id}" }
                        ) { cv ->
                            ServiceItemCard(
                                icon = Icons.Outlined.Description,
                                title = cv.title,
                                subtitle = "${cv.experience.size} experiencias · ${cv.skills.size} habilidades",
                                statusText = if (cv.isVisible) "Visible" else "Oculto",
                                accentColor = Color(0xFF0288D1),
                                onEdit = { onAction(DashboardAction.EditCv(cv.id)) },
                                onDelete = { onAction(DashboardAction.ConfirmDeleteCv(cv)) },
                                onShare = { onAction(DashboardAction.ShareCv(cv.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                
                // Shops section
                state.services.filterIsInstance<ServiceModule.ShopModule>().firstOrNull()?.let { shopModule ->
                    if (shopModule.shops.isNotEmpty()) {
                        item(key = "shops_header") {
                            SectionHeader(
                                title = "Tiendas",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = shopModule.shops,
                            key = { "shop_${it.id}" }
                        ) { shop ->
                            ServiceItemCard(
                                icon = Icons.Outlined.Inventory2,
                                title = shop.name,
                                subtitle = "${shop.products.size} productos · ${shop.categories.size} categorías",
                                statusText = if (shop.isActive) "Activo" else "Inactivo",
                                accentColor = Color(0xFF388E3C),
                                onEdit = { onAction(DashboardAction.EditShop(shop.id)) },
                                onDelete = { onAction(DashboardAction.ConfirmDeleteShop(shop)) },
                                onShare = { onAction(DashboardAction.ShareShop(shop.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
                
                // Invitations section
                state.services.filterIsInstance<ServiceModule.InvitationModule>().firstOrNull()?.let { invitationModule ->
                    if (invitationModule.invitations.isNotEmpty()) {
                        item(key = "invitations_header") {
                            SectionHeader(
                                title = "Invitaciones",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(
                            items = invitationModule.invitations,
                            key = { "invitation_${it.id}" }
                        ) { invitation ->
                            ServiceItemCard(
                                icon = Icons.Outlined.Mail,
                                title = invitation.eventName,
                                subtitle = "${invitation.responses.size} respuestas",
                                statusText = if (invitation.isActive) "Activa" else "Finalizada",
                                accentColor = Color(0xFFC2185B),
                                onEdit = { onAction(DashboardAction.EditInvitation(invitation.id)) },
                                onDelete = { onAction(DashboardAction.ConfirmDeleteInvitation(invitation)) },
                                onShare = { onAction(DashboardAction.ShareInvitation(invitation.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        state.deleteDialog?.let { dialog ->
            val (title, message) = when (dialog) {
                is DeleteDialogState.DeleteMenu -> "Eliminar menú" to "¿Estás seguro de que quieres eliminar el menú '${dialog.menu.name}'? Esta acción no se puede deshacer."
                is DeleteDialogState.DeletePortfolio -> "Eliminar portfolio" to "¿Estás seguro de que quieres eliminar el portfolio '${dialog.portfolio.title}'? Esta acción no se puede deshacer."
                is DeleteDialogState.DeleteCv -> "Eliminar currículum" to "¿Estás seguro de que quieres eliminar el CV '${dialog.cv.title}'? Esta acción no se puede deshacer."
                is DeleteDialogState.DeleteShop -> "Eliminar tienda" to "¿Estás seguro de que quieres eliminar la tienda '${dialog.shop.name}'? Esta acción no se puede deshacer."
                is DeleteDialogState.DeleteInvitation -> "Eliminar invitación" to "¿Estás seguro de que quieres eliminar la invitación '${dialog.invitation.eventName}'? Esta acción no se puede deshacer."
            }
            
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { onAction(DashboardAction.DismissDeleteDialog) },
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { onAction(DashboardAction.ConfirmDelete) },
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { onAction(DashboardAction.DismissDeleteDialog) }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sin servicios",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usa el botón + para crear tu primer servicio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
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