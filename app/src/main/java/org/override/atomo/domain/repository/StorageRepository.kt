package org.override.atomo.domain.repository

import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlin.time.Duration.Companion.minutes

interface StorageRepository {
    suspend fun uploadImage(userId: String, service: String, fileName: String, byteArray: ByteArray): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}
