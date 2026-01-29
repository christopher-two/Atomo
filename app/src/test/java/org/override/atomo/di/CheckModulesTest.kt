/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.jan.supabase.SupabaseClient
import io.mockk.mockk
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.di.data.DataModule
import org.override.atomo.di.data.DatabaseModule
import org.override.atomo.di.data.RepositoryModule
import org.override.atomo.di.data.SupabaseModule
import org.override.atomo.di.domain.UseCaseModule
import org.override.atomo.di.feature.FeaturesModule

class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinModules() {
        koinApplication {
            val mockedContext = mockk<Context>(relaxed = true)
            androidContext(mockedContext)
            
            // Provide mocks for components that fail in plain unit tests
            // or that should be mocked during validation to avoid side effects
            val testMocks = module {
                single<DataStore<Preferences>> { mockk(relaxed = true) }
                single<SupabaseClient> { mockk(relaxed = true) }
                single<AtomoDatabase> { mockk(relaxed = true) }
            }

            modules(
                FeaturesModule + DataModule + SupabaseModule +
                        DatabaseModule + RepositoryModule + UseCaseModule +
                        NavModule + testMocks
            )
        }.checkModules()
    }
}
