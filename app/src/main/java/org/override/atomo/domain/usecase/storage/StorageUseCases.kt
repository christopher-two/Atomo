package org.override.atomo.domain.usecase.storage

import org.override.atomo.domain.repository.StorageRepository

class UploadDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(userId: String, dishId: String, byteArray: ByteArray): Result<String> =
        repository.uploadImage(userId, "dish", "$dishId.jpg", byteArray)
}

class DeleteDishImageUseCase(private val repository: StorageRepository) {
    suspend operator fun invoke(imageUrl: String): Result<Unit> =
        repository.deleteImage(imageUrl)
}
