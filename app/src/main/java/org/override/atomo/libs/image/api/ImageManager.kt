/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.image.api

import android.net.Uri

interface ImageManager {
    /**
     * Compresses an image from a Uri and returns the compressed bytes.
     */
    suspend fun compressImage(uri: Uri): Result<ByteArray>

    /**
     * Resolves a Uri to a temporary file.
     */
    fun resolveUriToBytes(uri: Uri): Result<ByteArray>
}
