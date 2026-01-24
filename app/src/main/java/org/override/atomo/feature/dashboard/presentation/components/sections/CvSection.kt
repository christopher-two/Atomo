package org.override.atomo.feature.dashboard.presentation.components.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard
import org.override.atomo.feature.dashboard.presentation.components.base.SectionHeader

fun LazyListScope.cvSection(
    module: ServiceModule.CvModule,
    onAction: (DashboardAction) -> Unit
) {
    if (module.cvs.isNotEmpty()) {
        item(key = "cvs_header") {
            SectionHeader(
                title = "Currículums",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(
            items = module.cvs,
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
