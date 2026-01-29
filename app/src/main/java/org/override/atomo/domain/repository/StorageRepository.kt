package org.override.atomo.domain.repository

import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlin.time.Duration.Companion.minutes

interface StorageRepository {
    suspend fun uploadDishImage(dishId: String, byteArray: ByteArray): Result<String>
    suspend fun deleteDishImage(imageUrl: String): Result<Unit>
}
