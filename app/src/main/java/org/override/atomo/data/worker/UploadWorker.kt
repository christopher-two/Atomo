/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.ProfileRepository

/**
 * Worker to upload local changes to the server.
 * Queries local DB for unsynced items and pushes them.
 */
class UploadWorker(
    context: Context,
    params: WorkerParameters,
    private val menuRepository: MenuRepository,
    private val profileRepository: ProfileRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userId = inputData.getString("userId") ?: return@withContext Result.failure()

            // 1. Sync Menus (and children)
            menuRepository.syncUp(userId)
                .onFailure {
                    it.printStackTrace()
                    // We might want to return retry if it's a network error,
                    // but syncUp usually catches internal errors.
                    // If Result is failure, we should probably throw or return Retry to WorkManager.
                    throw it
                }

            // 2. Sync Profile
            profileRepository.syncUp(userId)
                .onFailure {
                    it.printStackTrace()
                    // Same logic as above
                }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
