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
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.ProfileRepository

class DataSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val menuRepository: MenuRepository,
    private val profileRepository: ProfileRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = inputData.getString("userId")
                ?: return Result.failure()

            // Grouping sync operations
            // We want to sync all critical data here.

            menuRepository.syncMenus(userId)
                .onFailure { it.printStackTrace() }

            profileRepository.syncProfile(userId)
                .onFailure { it.printStackTrace() }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
