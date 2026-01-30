/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module
import org.override.atomo.data.worker.DataSyncWorker
import org.override.atomo.data.worker.ProfileSyncWorker
import org.override.atomo.data.worker.UploadWorker

val workerModule = module {
    workerOf(::UploadWorker)
    workerOf(::DataSyncWorker)
    workerOf(::ProfileSyncWorker)
}
