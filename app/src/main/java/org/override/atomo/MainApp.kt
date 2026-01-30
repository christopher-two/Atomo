/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.override.atomo.di.NavModule
import org.override.atomo.di.data.DataModule
import org.override.atomo.di.data.DatabaseModule
import org.override.atomo.di.data.RepositoryModule
import org.override.atomo.di.data.SupabaseModule
import org.override.atomo.di.domain.UseCaseModule
import org.override.atomo.di.feature.FeaturesModule
import org.override.atomo.di.libs.LibModule
import org.override.atomo.di.workerModule

class MainApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            allowOverride(true)
            printLogger(Level.ERROR)
            androidLogger()
            androidContext(this@MainApp)
            modules(
                modules = FeaturesModule + DataModule + SupabaseModule +
                        DatabaseModule + RepositoryModule + UseCaseModule +
                        NavModule + LibModule + workerModule
            )
            workManagerFactory()
        }
    }
}