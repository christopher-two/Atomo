/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

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
import org.override.atomo.domain.model.Invitation
import org.override.atomo.feature.dashboard.presentation.components.form.AestheticTextField
import org.override.atomo.feature.dashboard.presentation.components.form.ColorSelector
import org.override.atomo.feature.dashboard.presentation.components.form.SectionTitle
import org.override.atomo.feature.dashboard.presentation.components.form.SwitchTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInvitationSheet(
    invitation: Invitation,
    onDismiss: () -> Unit,
    onSave: (Invitation) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var eventName by remember { mutableStateOf(invitation.eventName) }
    var description by remember { mutableStateOf(invitation.description ?: "") }
    var isActive by remember { mutableStateOf(invitation.isActive) }
    var primaryColor by remember { mutableStateOf(Color(android.graphics.Color.parseColor(invitation.primaryColor))) }
    
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
            SectionTitle("Editar Invitación")
            
            AestheticTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = "Nombre del Evento",
                placeholder = "Ej. Boda de Ana y Juan"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AestheticTextField(
                value = description,
                onValueChange = { description = it },
                label = "Detalles",
                placeholder = "Mensaje de bienvenida...",
                singleLine = false,
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SwitchTile(
                title = "Invitación Activa",
                description = "Disponible para confirmar asistencia",
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
                        invitation.copy(
                            eventName = eventName,
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
