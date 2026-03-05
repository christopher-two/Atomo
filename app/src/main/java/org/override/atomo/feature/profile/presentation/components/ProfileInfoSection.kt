/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Muestra el nombre, handle de usuario y fecha de creación del perfil.
 */
@Composable
fun ProfileInfoSection(
    username: String,
    displayName: String?,
    createdAt: Long,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayName ?: "@$username",
            style = MaterialTheme.typography.displaySmall.copy(
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            minLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (displayName != null) {
            Text(
                text = "@$username",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Joined ${dateFormat.format(Date(createdAt))}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileInfoSectionPreview() {
    MaterialTheme {
        ProfileInfoSection(
            username = "chris_dev",
            displayName = "Christopher",
            createdAt = System.currentTimeMillis()
        )
    }
}

