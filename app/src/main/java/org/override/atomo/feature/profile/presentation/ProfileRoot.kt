package org.override.atomo.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {}
        )
    }
}