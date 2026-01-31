/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.components.service.EditableSection
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuAction

@Composable
fun DigitalMenuEditor(
    menu: Menu,
    onAction: (DigitalMenuAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // General Info
        EditableSection(title = "General Information", isEditing = true) {
            AtomoTextField(
                value = menu.name,
                onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(name = it))) },
                label = { Text("Menu Title") },
                modifier = Modifier.fillMaxWidth()
            )
            AtomoTextField(
                value = menu.description ?: "",
                onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(description = it))) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
