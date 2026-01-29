package org.override.atomo.domain.usecase.storage

import org.override.atomo.domain.repository.StorageRepository

class UploadDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(dishId: String, byteArray: ByteArray): Result<String> =
        repository.uploadDishImage(dishId, byteArray)
}

class DeleteDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(imageUrl: String): Result<Unit> =
        repository.deleteDishImage(imageUrl)
}
