package org.override.atomo.core.ui.components.service

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServiceToolbar(
    expanded: Boolean = true,
    isEditing: Boolean,
    onBack: () -> Unit,
    onEditVerify: () -> Unit, // Callback to switch to edit or save
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        modifier = modifier.padding(16.dp),
    ) {
        // Back / Close
        IconButton(onClick = onBack) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        // Edit / Save Toggle
        IconButton(onClick = onEditVerify) {
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
    }
}
