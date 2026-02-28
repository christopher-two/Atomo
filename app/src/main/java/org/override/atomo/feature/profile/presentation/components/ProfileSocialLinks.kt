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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Muestra la lista de redes sociales del perfil.
 * No se renderiza si [socialLinks] es nulo o está vacío.
 */
@Composable
fun ProfileSocialLinks(
    socialLinks: Map<String, String>,
    modifier: Modifier = Modifier
) {
    if (socialLinks.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Social Links",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        socialLinks.forEach { (platform, url) ->
            ListItem(
                headlineContent = {
                    Text(platform.replaceFirstChar { it.uppercase() })
                },
                supportingContent = {
                    Text(url, maxLines = 1)
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSocialLinksPreview() {
    MaterialTheme {
        ProfileSocialLinks(
            socialLinks = mapOf(
                "twitter" to "https://twitter.com/chris_dev",
                "github" to "https://github.com/chris_dev"
            )
        )
    }
}

