package org.override.atomo.feature.digital_menu.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.domain.model.Dish

@Composable
fun DishDialog(
    dish: Dish?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String?) -> Unit
) {
    var name by remember { mutableStateOf(dish?.name ?: "") }
    var description by remember { mutableStateOf(dish?.description ?: "") }
    var price by remember { mutableStateOf(dish?.price?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (dish == null) "Add Dish" else "Edit Dish") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AtomoTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                AtomoTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                AtomoTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceVal = price.toDoubleOrNull() ?: 0.0
                onSave(name, description, priceVal, null)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
