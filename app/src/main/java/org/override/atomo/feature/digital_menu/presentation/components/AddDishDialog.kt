package org.override.atomo.feature.digital_menu.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoButton
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.domain.model.Dish

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDishDialog(
    dishToEdit: Dish? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, price: Double, imageUrl: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var name by remember { mutableStateOf(dishToEdit?.name ?: "") }
    var description by remember { mutableStateOf(dishToEdit?.description ?: "") }
    var price by remember { mutableStateOf(dishToEdit?.price?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(dishToEdit?.imageUrl ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (dishToEdit == null) "Add New Dish" else "Edit Dish",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AtomoTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Dish Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AtomoTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AtomoTextField(
                value = price,
                onValueChange = { 
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        price = it 
                    }
                },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // TODO: Implement proper Image Picker
            Spacer(modifier = Modifier.height(16.dp))
            
            AtomoTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AtomoButton(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                         onConfirm(name, description, priceValue, imageUrl.takeIf { it.isNotBlank() })
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text(if (dishToEdit == null) "Add Dish" else "Save Changes")
            }
        }
    }
}
