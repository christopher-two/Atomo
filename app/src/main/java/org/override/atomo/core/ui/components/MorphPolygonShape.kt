/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.toPath

/**
 * A custom [Shape] that draws a morphed polygon based on a [Morph] object.
 * Used for dynamic shape animations.
 *
 * @property morph The morph object defining the shape transition.
 * @property percentage The current progress of the morph (0.0 to 1.0).
 */
class MorphPolygonShape(
    private val morph: Morph,
    private val percentage: Float
) : Shape {

    // Usamos explícitamente la Matrix nativa de Android para tener postScale/postTranslate
    private val matrix = android.graphics.Matrix()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // 1. Obtener el Path nativo (android.graphics.Path)
        val path = morph.toPath(percentage)

        // 2. Limpiar la matriz
        matrix.reset()

        // 3. ESCALADO:
        // Las formas vienen en un rango de aprox [-1 a 1] (tamaño 2).
        // Dividimos el tamaño del contenedor entre 2 para obtener el factor de escala.
        val scale = size.minDimension / 2f

        // 4. TRASLACIÓN:
        // Movemos el origen (0,0) al centro de tu Box.
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Aplicamos las transformaciones en orden: primero escalar, luego mover.
        matrix.postScale(scale, scale)
        matrix.postTranslate(centerX, centerY)

        // 5. Transformamos el path nativo y LUEGO lo convertimos a Compose
        path.transform(matrix)

        return Outline.Generic(path.asComposePath())
    }
}