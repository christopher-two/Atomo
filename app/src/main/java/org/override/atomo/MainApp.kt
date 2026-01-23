package org.override.atomo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.override.atomo.di.data.DataModule
import org.override.atomo.di.data.SupabaseModule
import org.override.atomo.di.feature.FeaturesModule

class MainApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            allowOverride(true)
            printLogger(Level.ERROR)
            androidLogger()
            androidContext(this@MainApp)
            modules(
                modules = FeaturesModule + DataModule + SupabaseModule
            )
        }
    }
}