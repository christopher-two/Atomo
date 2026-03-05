/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.storage.domain.usecase.storage

import org.override.atomo.feature.storage.domain.repository.StorageRepository

class UploadDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(userId: String, dishId: String, byteArray: ByteArray): Result<String> =
        repository.uploadImage(userId, "dish", "$dishId.jpg", byteArray)
}

class DeleteDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(imageUrl: String): Result<Unit> =
        repository.deleteImage(imageUrl)
}
