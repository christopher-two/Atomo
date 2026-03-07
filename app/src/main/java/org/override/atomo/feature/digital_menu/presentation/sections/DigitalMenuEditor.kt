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
import org.override.atomo.feature.digital_menu.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuAction
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuOverlay
import org.override.atomo.feature.digital_menu.domain.model.MenuTemplate
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun DigitalMenuEditor(
    menu: Menu,
    templates: List<MenuTemplate>,
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
        
        EditableSection(title = "Template", isEditing = true) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(templates) { template ->
                    val isSelected = template.id == menu.templateId
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { onAction(DigitalMenuAction.UpdateTemplate(template.id)) }
                            .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium) else Modifier),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column {
                            AsyncImage(
                                model = template.previewImageUrl,
                                contentDescription = template.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = template.name,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (!template.description.isNullOrBlank()) {
                                Text(
                                    text = template.description,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .padding(bottom = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
