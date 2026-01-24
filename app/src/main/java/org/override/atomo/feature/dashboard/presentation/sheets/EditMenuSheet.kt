package org.override.atomo.feature.dashboard.presentation.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.dashboard.presentation.components.form.AestheticTextField
import org.override.atomo.feature.dashboard.presentation.components.form.ColorSelector
import org.override.atomo.feature.dashboard.presentation.components.form.SectionTitle
import org.override.atomo.feature.dashboard.presentation.components.form.SwitchTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenuSheet(
    menu: Menu,
    onDismiss: () -> Unit,
    onSave: (Menu) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var name by remember { mutableStateOf(menu.name) }
    var description by remember { mutableStateOf(menu.description ?: "") }
    var isActive by remember { mutableStateOf(menu.isActive) }
    var primaryColor by remember { mutableStateOf(Color(android.graphics.Color.parseColor(menu.primaryColor))) }
    
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
            SectionTitle("Editar Menú")
            
            AestheticTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre del Menú",
                placeholder = "Ej. Menú Principal"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AestheticTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción (Opcional)",
                placeholder = "Breve descripción de tu menú",
                singleLine = false,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SwitchTile(
                title = "Menú Activo",
                description = "Visible para tus clientes si está activado",
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ColorSelector(
                selectedColor = primaryColor,
                onColorSelected = { primaryColor = it },
                label = "Color Principal"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    onSave(
                        menu.copy(
                            name = name,
                            description = description.ifBlank { null },
                            isActive = isActive,
                            primaryColor = String.format("#%06X", (0xFFFFFF and primaryColor.value.toInt()))
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Guardar Cambios")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
