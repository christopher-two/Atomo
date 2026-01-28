/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

/**
 * A custom card component for the Atomo design system.
 * Wraps [ElevatedCard] with default styling and behaviors.
 * Supports both clickable and non-clickable variants.
 *
 * @param modifier Modifier for styling.
 * @param shape Shape of the card.
 * @param colors Colors for the card.
 * @param elevation Elevation for the card.
 * @param onClick Optional callback when the card is clicked. If null, card is not clickable.
 * @param content The content of the card.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AtomoCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    } else {
        ElevatedCard(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    }
}
