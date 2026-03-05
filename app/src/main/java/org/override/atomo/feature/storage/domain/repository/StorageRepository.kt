/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.storage.domain.repository

interface StorageRepository {
    suspend fun uploadImage(userId: String, service: String, fileName: String, byteArray: ByteArray): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}
