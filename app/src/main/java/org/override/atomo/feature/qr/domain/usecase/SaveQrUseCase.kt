/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.qr.domain.usecase

import android.graphics.Bitmap
import org.override.atomo.feature.qr.domain.repository.QrRepository

class SaveQrUseCase(
    private val repository: QrRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, text: String?): Result<Unit> {
        return repository.saveQrCode(bitmap, text)
    }
}
