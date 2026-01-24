package org.override.atomo.feature.digital_menu.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoButton
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.digital_menu.presentation.components.AddDishDialog
import org.override.atomo.feature.digital_menu.presentation.components.DishItem

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalMenuScreen(
    state: DigitalMenuState,
    onAction: (DigitalMenuAction) -> Unit,
) {
    if (state.isDishDialogVisible) {
        AddDishDialog(
            dishToEdit = state.dishToEdit,
            onDismiss = { onAction(DigitalMenuAction.CloseDishDialog) },
            onConfirm = { name, desc, price, img ->
                onAction(DigitalMenuAction.SaveDish(name, desc, price, img))
            }
        )
    }

    AtomoScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Menu") },
                navigationIcon = {
                    IconButton(onClick = { onAction(DigitalMenuAction.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(DigitalMenuAction.OpenAddDishDialog) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Dish")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp), // Space for FAB
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Info Section
            item {
                Text(
                    text = "General Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                
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
            }

            // Dishes Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dishes (${state.dishes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (state.dishes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No dishes yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap the + button to add your first dish",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                items(state.dishes) { dish ->
                    DishItem(
                        dish = dish,
                        onEdit = { onAction(DigitalMenuAction.OpenEditDishDialog(it)) },
                        onDelete = { onAction(DigitalMenuAction.DeleteDish(it)) }
                    )
                }
            }
            
            // Save Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AtomoButton(
                    onClick = { onAction(DigitalMenuAction.SaveMenu) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && state.menuName.isNotBlank()
                ) {
                    Text(if (state.isLoading) "Saving..." else "Create Dashboard")
                }
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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