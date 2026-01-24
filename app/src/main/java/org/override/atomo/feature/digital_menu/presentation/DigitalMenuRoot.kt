package org.override.atomo.feature.digital_menu.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoButton
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun DigitalMenuRoot(
    viewModel: DigitalMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DigitalMenuScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DigitalMenuScreen(
    state: DigitalMenuState,
    onAction: (DigitalMenuAction) -> Unit,
) {
    AtomoScaffold(
        topBar = {
            Text(
                text = "Create Menu",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AtomoTextField(
                value = state.menuName,
                onValueChange = { onAction(DigitalMenuAction.UpdateName(it)) },
                label = { Text("Menu Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AtomoTextField(
                value = state.menuDescription,
                onValueChange = { onAction(DigitalMenuAction.UpdateDescription(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AtomoButton(
                onClick = { onAction(DigitalMenuAction.SaveMenu) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Saving..." else "Create Menu")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        DigitalMenuScreen(
            state = DigitalMenuState(),
            onAction = {}
        )
    }
}