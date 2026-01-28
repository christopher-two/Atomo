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
import io.mockk.mockk
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
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
            modules(
                FeaturesModule + DataModule + SupabaseModule +
                        DatabaseModule + RepositoryModule + UseCaseModule +
                        NavModule
            )
        }.checkModules()
    }
}
