package org.override.atomo.feature.dashboard.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun DashboardRoot(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        DashboardScreen(
            state = DashboardState(),
            onAction = {}
        )
    }
}