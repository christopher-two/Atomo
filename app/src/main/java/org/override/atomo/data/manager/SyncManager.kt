/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.manager

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.override.atomo.data.worker.DataSyncWorker
import org.override.atomo.data.worker.ProfileSyncWorker
import org.override.atomo.data.worker.UploadWorker
import java.util.concurrent.TimeUnit

class SyncManager(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleInitialSync(userId: String) {
        val data = Data.Builder().putString("userId", userId).build()

        val request = OneTimeWorkRequestBuilder<ProfileSyncWorker>()
            .setInputData(data)
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueue(request)

        scheduleDataSync(userId)
    }

    fun scheduleDataSync(userId: String) {
        val data = Data.Builder().putString("userId", userId).build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "data_sync_$userId",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleUpload(userId: String) {
        val data = Data.Builder().putString("userId", userId).build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10, TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "upload_$userId",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request
        )
    }
}
