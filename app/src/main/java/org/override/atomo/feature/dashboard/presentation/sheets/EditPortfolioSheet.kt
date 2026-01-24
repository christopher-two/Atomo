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
import androidx.compose.ui.unit.dp
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.feature.dashboard.presentation.components.form.AestheticTextField
import org.override.atomo.feature.dashboard.presentation.components.form.SectionTitle
import org.override.atomo.feature.dashboard.presentation.components.form.SwitchTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPortfolioSheet(
    portfolio: Portfolio,
    onDismiss: () -> Unit,
    onSave: (Portfolio) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var title by remember { mutableStateOf(portfolio.title) }
    var description by remember { mutableStateOf(portfolio.description ?: "") }
    var isVisible by remember { mutableStateOf(portfolio.isVisible) }
    
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
            SectionTitle("Editar Portfolio")
            
            AestheticTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título del Proyecto",
                placeholder = "Ej. Mi Portfolio 2024"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AestheticTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                placeholder = "Describe tu trabajo...",
                singleLine = false,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SwitchTile(
                title = "Visible al público",
                description = "Permite que otros vean tu trabajo",
                checked = isVisible,
                onCheckedChange = { isVisible = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    onSave(
                        portfolio.copy(
                            title = title,
                            description = description.ifBlank { null },
                            isVisible = isVisible
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
