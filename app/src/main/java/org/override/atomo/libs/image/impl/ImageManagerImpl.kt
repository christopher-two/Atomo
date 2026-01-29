/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.image.impl

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.override.atomo.libs.image.api.ImageManager
import java.io.File
import java.io.FileOutputStream

class ImageManagerImpl(
    private val context: Context
) : ImageManager {

    override suspend fun compressImage(uri: Uri): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = resolveUriToFile(uri) ?: throw Exception("Could not resolve URI to file")
            val compressedFile = Compressor.compress(context, sourceFile) {
                default(format = Bitmap.CompressFormat.JPEG)
                resolution(1280, 1280)
                quality(80)
            }
            compressedFile.readBytes()
        }
    }

    override fun resolveUriToBytes(uri: Uri): Result<ByteArray> = runCatching {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw Exception("Could not open input stream for URI")
    }

    private fun resolveUriToFile(uri: Uri): File? {
        if (uri.scheme == "file") return runCatching { uri.toFile() }.getOrNull()

        val tempFile = runCatching { 
            File.createTempFile("atomo_img_", ".tmp", context.cacheDir) 
        }.getOrNull() ?: return null

        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        }.getOrNull()
    }
}
