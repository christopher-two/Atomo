package org.override.atomo.feature.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        SettingsScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}