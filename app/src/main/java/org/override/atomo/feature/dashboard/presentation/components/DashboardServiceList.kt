/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.dashboard.domain.model.ServiceModule
import org.override.atomo.feature.dashboard.presentation.DashboardAction

/**
 * Extiende [LazyListScope] renderizando una [ServiceCard] por cada módulo activo.
 * Se llama directamente dentro de un bloque `LazyColumn { }`.
 */
fun LazyListScope.dashboardServiceItems(
    services: List<ServiceModule>,
    onAction: (DashboardAction) -> Unit
) {
    items(services) { module ->
        if (module.isActive) {
            when (module) {
                is ServiceModule.MenuModule -> {
                    val menu = module.menus.first()
                    ServiceCard(
                        title = menu.name,
                        subtitle = "${module.totalDishes} Platillos",
                        icon = Icons.Filled.RestaurantMenu,
                        onPreviewClick = { onAction(DashboardAction.PreviewService(ServiceType.DIGITAL_MENU, menu.id)) },
                        onShareClick = { onAction(DashboardAction.ShareService(ServiceType.DIGITAL_MENU, menu.id)) },
                        onQrClick = { onAction(DashboardAction.ShowQR(ServiceType.DIGITAL_MENU, menu.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ServiceModule.ShopModule -> {
                    val shop = module.shops.first()
                    ServiceCard(
                        title = shop.name,
                        subtitle = "${module.totalProducts} Productos",
                        icon = Icons.Filled.ShoppingBag,
                        onPreviewClick = { onAction(DashboardAction.PreviewService(ServiceType.SHOP, shop.id)) },
                        onShareClick = { onAction(DashboardAction.ShareService(ServiceType.SHOP, shop.id)) },
                        onQrClick = { onAction(DashboardAction.ShowQR(ServiceType.SHOP, shop.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ServiceModule.CvModule -> {
                    val cv = module.cvs.first()
                    ServiceCard(
                        title = cv.title,
                        subtitle = "${module.totalSkills} Habilidades, ${module.totalExperiences} Exp.",
                        icon = Icons.Filled.Description,
                        onPreviewClick = { onAction(DashboardAction.PreviewService(ServiceType.CV, cv.id)) },
                        onShareClick = { onAction(DashboardAction.ShareService(ServiceType.CV, cv.id)) },
                        onQrClick = { onAction(DashboardAction.ShowQR(ServiceType.CV, cv.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ServiceModule.PortfolioModule -> {
                    val portfolio = module.portfolios.first()
                    ServiceCard(
                        title = portfolio.title,
                        subtitle = "${module.totalItems} Proyectos",
                        icon = Icons.Filled.Description,
                        onPreviewClick = { onAction(DashboardAction.PreviewService(ServiceType.PORTFOLIO, portfolio.id)) },
                        onShareClick = { onAction(DashboardAction.ShareService(ServiceType.PORTFOLIO, portfolio.id)) },
                        onQrClick = { onAction(DashboardAction.ShowQR(ServiceType.PORTFOLIO, portfolio.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ServiceModule.InvitationModule -> {
                    val invitation = module.invitations.first()
                    ServiceCard(
                        title = invitation.eventName,
                        subtitle = invitation.description ?: "Sin descripción",
                        icon = Icons.Filled.Description,
                        onPreviewClick = { onAction(DashboardAction.PreviewService(ServiceType.INVITATION, invitation.id)) },
                        onShareClick = { onAction(DashboardAction.ShareService(ServiceType.INVITATION, invitation.id)) },
                        onQrClick = { onAction(DashboardAction.ShowQR(ServiceType.INVITATION, invitation.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

