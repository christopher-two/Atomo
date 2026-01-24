package org.override.atomo.feature.dashboard.presentation.components.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard
import org.override.atomo.feature.dashboard.presentation.components.base.SectionHeader

fun LazyListScope.portfolioSection(
    module: ServiceModule.PortfolioModule,
    onAction: (DashboardAction) -> Unit
) {
    if (module.portfolios.isNotEmpty()) {
        item(key = "portfolios_header") {
            SectionHeader(
                title = "Portfolios",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(
            items = module.portfolios,
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
