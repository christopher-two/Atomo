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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.override.atomo.R

/**
 * Encapsulates the logic of drawing a Compose Painter
 * and a text string into a native Android Bitmap.
 */
suspend fun generateQrBitmap(
    context: Context,
    painter: Painter,
    dataText: String?,
    backgroundColor: androidx.compose.ui.graphics.Color
): Bitmap = withContext(Dispatchers.IO) {
    val width = 1080
    val height = 1350
    val bitmap = createBitmap(width, height)
    val androidCanvas = AndroidCanvas(bitmap)
    androidCanvas.drawColor(backgroundColor.toArgb())

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

    val paintColor = if (backgroundColor.luminance() < 0.5f) AndroidColor.WHITE else "#3C1A06".toColorInt()
    val drawable = ContextCompat.getDrawable(context, R.drawable.logo_atomo_app_monochrome)
    
    if (drawable != null) {
        val logoSize = 140
        val logoLeft = ((width - logoSize) / 2f).toInt()
        val logoTop = (qrY + qrSize + 100f).toInt()
        
        drawable.setBounds(logoLeft, logoTop, logoLeft + logoSize, logoTop + logoSize)
        DrawableCompat.setTint(drawable, paintColor)
        drawable.draw(androidCanvas)
        
        val paint = Paint().apply {
            color = paintColor
            textSize = 65f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            letterSpacing = 0.05f
        }
        val textY = logoTop + logoSize + 85f
        androidCanvas.drawText("Atomo", width / 2f, textY, paint)
    }

    bitmap
}
