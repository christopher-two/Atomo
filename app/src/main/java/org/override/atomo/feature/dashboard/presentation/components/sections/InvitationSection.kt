package org.override.atomo.feature.dashboard.presentation.components.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.ServiceModule
import org.override.atomo.feature.dashboard.presentation.components.ServiceItemCard
import org.override.atomo.feature.dashboard.presentation.components.base.SectionHeader

fun LazyListScope.invitationSection(
    module: ServiceModule.InvitationModule,
    onAction: (DashboardAction) -> Unit
) {
    if (module.invitations.isNotEmpty()) {
        item(key = "invitations_header") {
            SectionHeader(
                title = "Invitaciones",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(
            items = module.invitations,
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
