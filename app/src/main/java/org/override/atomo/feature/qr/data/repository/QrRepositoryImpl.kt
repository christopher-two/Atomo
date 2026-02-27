/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.override.atomo.feature.qr.domain.repository.QrRepository
import java.io.OutputStream

class QrRepositoryImpl(
    private val context: Context
) : QrRepository {

    override suspend fun saveQrCode(bitmap: Bitmap, text: String?): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
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
                }

                fos?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "QR Guardado en Galería", Toast.LENGTH_SHORT).show()
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
                Result.failure(e)
            }
        }
    }
}
