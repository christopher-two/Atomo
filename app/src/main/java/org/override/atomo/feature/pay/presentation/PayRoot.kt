package org.override.atomo.feature.pay.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun PayRoot(
    viewModel: PayViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PayScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PayScreen(
    state: PayState,
    onAction: (PayAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        PayScreen(
            state = PayState(),
            onAction = {}
        )
    }
}