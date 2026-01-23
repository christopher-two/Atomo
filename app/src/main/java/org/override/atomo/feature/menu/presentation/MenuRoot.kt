package org.override.atomo.feature.menu.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun MenuRoot(
    viewModel: MenuViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MenuScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun MenuScreen(
    state: MenuState,
    onAction: (MenuAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        MenuScreen(
            state = MenuState(),
            onAction = {}
        )
    }
}