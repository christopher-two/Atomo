package org.override.atomo.feature.dashboard.presentation.components.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard
import org.override.atomo.feature.dashboard.presentation.components.base.SectionHeader

fun LazyListScope.shopSection(
    module: ServiceModule.ShopModule,
    onAction: (DashboardAction) -> Unit
) {
    if (module.shops.isNotEmpty()) {
        item(key = "shops_header") {
            SectionHeader(
                title = "Tiendas",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(
            items = module.shops,
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
