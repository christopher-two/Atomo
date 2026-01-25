package org.override.atomo.feature.home.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.dashboard.presentation.DashboardScreen
import org.override.atomo.feature.home.presentation.components.HomeScaffold
import org.override.atomo.feature.navigation.wrapper.WrapperHomeNavigation

@Composable
fun HomeRoot(
    viewModel: HomeViewModel,
    snackbarManager: SnackbarManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        snackbarManager = snackbarManager,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    state: HomeState,
    snackbarManager: SnackbarManager,
    onAction: (HomeAction) -> Unit,
) {
    HomeScaffold(
        snackbarManager = snackbarManager,
        state = state,
        content = {
            WrapperHomeNavigation()
        },
        onAction = onAction
    )
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        HomeScreen(
            state = HomeState(),
            snackbarManager = SnackbarManager(),
            onAction = {}
        )
    }
}