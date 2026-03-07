package org.override.atomo.feature.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.onboarding.presentation.DishInput
import org.override.atomo.feature.onboarding.presentation.OnboardingAction
import org.override.atomo.feature.onboarding.presentation.OnboardingState

@Composable
fun MenuItemsStepContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var newCategoryName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.categories) { category ->
                CategorySection(
                    categoryName = category,
                    dishes = state.dishes.filter { it.categoryName == category },
                    onAction = onAction
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("Nueva categoría (Ej: Bebidas)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (newCategoryName.isNotBlank() && !state.categories.contains(newCategoryName)) {
                                onAction(OnboardingAction.AddCategory(newCategoryName))
                                newCategoryName = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onAction(OnboardingAction.PreviousStep) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar"
                )
            }

            Button(
                onClick = { onAction(OnboardingAction.NextStep) },
                enabled = state.canProceed
            ) {
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun CategorySection(
    categoryName: String,
    dishes: List<DishInput>,
    onAction: (OnboardingAction) -> Unit
) {
    var newDishName by remember { mutableStateOf("") }
    var newDishPrice by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = categoryName, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onAction(OnboardingAction.RemoveCategory(categoryName)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            dishes.forEach { dish ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("- ${dish.name}")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$${dish.price}")
                        IconButton(onClick = { onAction(OnboardingAction.RemoveDish(dish)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar platillo", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newDishName,
                    onValueChange = { newDishName = it },
                    placeholder = { Text("Platillo") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = newDishPrice,
                    onValueChange = { newDishPrice = it },
                    placeholder = { Text("Precio") },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                TextButton(
                    onClick = {
                        val price = newDishPrice.toDoubleOrNull()
                        if (newDishName.isNotBlank() && price != null) {
                            onAction(
                                OnboardingAction.AddDish(
                                    DishInput(name = newDishName, price = price, categoryName = categoryName)
                                )
                            )
                            newDishName = ""
                            newDishPrice = ""
                        }
                    }
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}
