/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.ui.components.service

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServiceToolbar(
    isEditing: Boolean,
    onEditVerify: () -> Unit, // Callback to switch to edit or save
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onCancel: (() -> Unit)? = null,
    onPreview: () -> Unit,
    onDelete: (() -> Unit)? = null,
    saveEnabled: Boolean = true,
    additionalActions: (@Composable () -> Unit)? = null
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        modifier = modifier.padding(16.dp),
    ) {
        // Additional Actions (Custom)
        additionalActions?.invoke()

        // Cancel Button (Only in edit mode)
        if (isEditing && onCancel != null) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel"
                )
            }
        }

        // Edit / Save Toggle
        IconButton(
            onClick = onEditVerify,
            enabled = !isEditing || saveEnabled
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                contentDescription = if (isEditing) "Save" else "Edit"
            )
        }

        // Preview (Always available)
        IconButton(onClick = onPreview) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "Preview"
            )
        }
        
        // Delete
        if (!isEditing && onDelete != null) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
