package org.override.atomo.feature.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.override.atomo.feature.profile.presentation.components.ProfileDetailView
import org.override.atomo.feature.profile.presentation.components.ProfileEditView
import org.override.atomo.feature.profile.presentation.components.ProfileShimmer

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading && state.profile == null) {
            ProfileShimmer()
        } else if (state.error != null && state.profile == null) {
            Text(
                text = state.error,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error
            )
        } else if (state.profile != null) {
            if (state.isEditing) {
                ProfileEditView(
                    state = state,
                    onAction = onAction
                )
            } else {
                ProfileDetailView(
                    profile = state.profile,
                    onEditClick = { onAction(ProfileAction.EnterEditMode) },
                    onSyncClick = { onAction(ProfileAction.Refresh) }
                )
            }
        }
    }
}
