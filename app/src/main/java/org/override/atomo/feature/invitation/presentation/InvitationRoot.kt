package org.override.atomo.feature.invitation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Invitation

@Composable
fun InvitationRoot(
    viewModel: InvitationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    InvitationScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun InvitationScreen(
    state: InvitationState,
    onAction: (InvitationAction) -> Unit,
) {
    AtomoScaffold(
        topBar = {
            Text(
                text = "My Invitations",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(InvitationAction.CreateInvitation) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Invitation")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(state.invitations) { invitation ->
                InvitationItem(invitation = invitation, onAction = onAction)
            }
        }
    }
}

@Composable
fun InvitationItem(
    invitation: Invitation,
    onAction: (InvitationAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(InvitationAction.OpenInvitation(invitation.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = invitation.eventName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (invitation.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(InvitationAction.DeleteInvitation(invitation.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Invitation")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        InvitationScreen(
            state = InvitationState(),
            onAction = {}
        )
    }
}