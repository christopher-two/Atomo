package org.override.atomo.feature.dashboard.presentation.sheets.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.override.atomo.domain.model.Dish
import org.override.atomo.feature.dashboard.presentation.components.form.AestheticTextField
import org.override.atomo.feature.dashboard.presentation.components.form.SectionTitle
import org.override.atomo.feature.dashboard.presentation.components.form.SwitchTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishSheet(
    dish: Dish?,
    menuId: String,
    onDismiss: () -> Unit,
    onSave: (Dish) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var name by remember { mutableStateOf(dish?.name ?: "") }
    var description by remember { mutableStateOf(dish?.description ?: "") }
    var price by remember { mutableStateOf(dish?.price?.toString() ?: "") }
    var isVisible by remember { mutableStateOf(dish?.isVisible ?: true) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle(if (dish == null) "Nuevo Platillo" else "Editar Platillo")
            
            AestheticTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre del platillo",
                placeholder = "Ej. Tacos al Pastor"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AestheticTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                placeholder = "Ingredientes, preparación...",
                singleLine = false,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AestheticTextField(
                value = price,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) price = it },
                label = "Precio",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SwitchTile(
                title = "Disponible",
                description = "Visible en el menú digital",
                checked = isVisible,
                onCheckedChange = { isVisible = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val newDish = dish?.copy(
                        name = name,
                        description = description.ifBlank { null },
                        price = price.toDoubleOrNull() ?: 0.0,
                        isVisible = isVisible
                    ) ?: Dish(
                        id = "", // Backend/UseCase will handle ID if empty
                        menuId = menuId,
                        categoryId = null,
                        name = name,
                        description = description.ifBlank { null },
                        price = price.toDoubleOrNull() ?: 0.0,
                        imageUrl = null,
                        isVisible = isVisible,
                        sortOrder = 0,
                        createdAt = System.currentTimeMillis()
                    )
                    onSave(newDish)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (dish == null) "Crear Platillo" else "Guardar Cambios")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
