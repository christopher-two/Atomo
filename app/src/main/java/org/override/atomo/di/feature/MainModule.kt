/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.feature

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.override.atomo.feature.main.MainViewModel

val MainModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
}
