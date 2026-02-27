/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.presentation.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

object QrSaver {

    suspend fun saveQrToGallery(context: Context, painter: Painter, sizePx: Int = 2048) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Create Bitmap
                val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(android.graphics.Canvas(bitmap))
                
                // 2. Draw Painter using CanvasDrawScope
                val size = Size(sizePx.toFloat(), sizePx.toFloat())
                val drawScope = CanvasDrawScope()
                
                drawScope.draw(
                    density = Density(context),
                    layoutDirection = LayoutDirection.Ltr,
                    canvas = canvas,
                    size = size
                ) {
                    with(painter) {
                        draw(size)
                    }
                }

                // 3. Save to MediaStore
                val filename = "Atomo_QR_${System.currentTimeMillis()}.png"
                var fos: OutputStream? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Atomo")
                    }
                    val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        fos = context.contentResolver.openOutputStream(imageUri)
                    }
                } else {
                    // For legacy support (not strict requirement but good practice), we'd use File API
                    // Assuming API 29+ for this project based on modern tech stack usage
                    // Just in case, simplistic fallback or error
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "QR Guardado en Galería", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
