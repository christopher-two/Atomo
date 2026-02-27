/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Encapsulates the logic of drawing a Compose Painter
 * and a text string into a native Android Bitmap.
 */
suspend fun generateQrBitmap(
    context: Context,
    painter: Painter,
    dataText: String?
): Bitmap = withContext(Dispatchers.IO) {
    val width = 1080
    val height = 1350
    val bitmap = createBitmap(width, height)
    val androidCanvas = AndroidCanvas(bitmap)
    androidCanvas.drawColor(AndroidColor.WHITE)

    val canvas = Canvas(androidCanvas)
    val qrSize = 800f
    val qrX = (width - qrSize) / 2f
    val qrY = 200f

    val drawScope = CanvasDrawScope()
    drawScope.draw(
        density = Density(context),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        translate(left = qrX, top = qrY) {
            with(painter) {
                draw(Size(qrSize, qrSize))
            }
        }
    }

    if (!dataText.isNullOrBlank()) {
        val paint = Paint().apply {
            color = "#3C1A06".toColorInt()
            textSize = 80f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.05f
        }
        val textY = qrY + qrSize + 160f
        val displayText = dataText.removePrefix("https://").removePrefix("http://").uppercase()
        androidCanvas.drawText(displayText, width / 2f, textY, paint)
    }

    bitmap
}
