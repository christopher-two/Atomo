package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import org.override.atomo.domain.repository.StorageRepository
import java.util.UUID

class StorageRepositoryImpl(
    private val supabase: SupabaseClient
) : StorageRepository {

    override suspend fun uploadImage(
        userId: String,
        service: String,
        fileName: String,
        byteArray: ByteArray
    ): Result<String> = runCatching {
        val bucket = supabase.storage.from("atomo")
        val path = "$userId/$service/$fileName"
        
        bucket.upload(path, byteArray) {
            upsert = true
        }
        
        bucket.publicUrl(path)
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> = runCatching {
        // Robust extraction: Extract everything after the bucket name "atomo/"
        // Standard URL: https://[project].supabase.co/storage/v1/object/public/atomo/userId/service/fileName.jpg
        val bucketName = "atomo"
        val delimiter = "/$bucketName/"
        
        if (!imageUrl.contains(delimiter)) {
             throw Exception("Invalid storage URL: bucket '$bucketName' not found in path")
        }
        
        val path = imageUrl.substringAfter(delimiter)
        val bucket = supabase.storage.from(bucketName)
        bucket.delete(listOf(path))
    }
}
