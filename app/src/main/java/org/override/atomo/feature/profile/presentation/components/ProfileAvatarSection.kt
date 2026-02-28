/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Muestra el avatar de perfil de forma estática con forma circular.
 * Si no hay [avatarUrl], muestra la inicial del [displayName] o "@" como fallback.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileAvatarSection(
    avatarUrl: String?,
    displayName: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialShapes.Cookie12Sided.toShape()
            ),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(160.dp)
                    .clip(MaterialShapes.Cookie7Sided.toShape()),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName?.firstOrNull()?.uppercase() ?: "@",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarSectionPreview() {
    MaterialTheme {
        ProfileAvatarSection(
            avatarUrl = null,
            displayName = "Christopher"
        )
    }
}

