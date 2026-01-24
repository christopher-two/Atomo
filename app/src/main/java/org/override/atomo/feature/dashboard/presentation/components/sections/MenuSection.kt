package org.override.atomo.feature.dashboard.presentation.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard
import org.override.atomo.feature.dashboard.presentation.components.base.SectionHeader
import org.override.atomo.feature.dashboard.presentation.components.rows.MiniItemCard
import org.override.atomo.feature.dashboard.presentation.components.rows.ServiceContentRow

fun LazyListScope.menuSection(
    module: ServiceModule.MenuModule,
    onAction: (DashboardAction) -> Unit
) {
    if (module.menus.isNotEmpty()) {
        item(key = "menus_header") {
            SectionHeader(
                title = "Menús Digitales",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(
            items = module.menus,
            key = { "menu_${it.id}" }
        ) { menu ->
            Column {
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

                ServiceContentRow(
                    title = "Platillos",
                    items = menu.dishes,
                    onAddClick = { onAction(DashboardAction.AddDish(menu.id)) },
                    onItemClick = { dish -> onAction(DashboardAction.EditDish(dish)) },
                    itemContent = { dish ->
                        MiniItemCard(
                            title = dish.name,
                            subtitle = "$${dish.price}"
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
