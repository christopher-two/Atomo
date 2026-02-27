/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.domain.repository

import android.graphics.Bitmap

interface QrRepository {
    suspend fun saveQrCode(bitmap: Bitmap, text: String?): Result<Unit>
}
