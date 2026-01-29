package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import org.override.atomo.domain.repository.StorageRepository
import java.util.UUID

class StorageRepositoryImpl(
    private val supabase: SupabaseClient
) : StorageRepository {

    override suspend fun uploadDishImage(dishId: String, byteArray: ByteArray): Result<String> = runCatching {
        val bucket = supabase.storage.from("dish-images")
        val path = "$dishId.jpg"
        
        // Correct way to upsert in newer Supabase Storage KT versions or handle overwrite
        bucket.upload(path, byteArray) {
            upsert = true
        }
        
        bucket.publicUrl(path)
    }

    override suspend fun deleteDishImage(imageUrl: String): Result<Unit> = runCatching {
        // Simple extraction logic, assuming standard Supabase URL format
        // This is a naive implementation, robust logic would parse the URL properly
        // Expected URL: https://.../storage/v1/object/public/dish-images/dishId.jpg
        val fileName = imageUrl.substringAfterLast("/")
        val bucket = supabase.storage.from("dish-images")
        bucket.delete(listOf(fileName))
    }
}
