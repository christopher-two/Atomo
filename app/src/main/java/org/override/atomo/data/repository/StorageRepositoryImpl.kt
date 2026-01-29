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
        // Extract path after bucket name "atomo"
        // Standard URL: https://.../storage/v1/object/public/atomo/userId/service/fileName.jpg
        val path = imageUrl.substringAfter("/atomo/")
        val bucket = supabase.storage.from("atomo")
        bucket.delete(listOf(path))
    }
}
