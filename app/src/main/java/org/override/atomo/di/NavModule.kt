/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.RootNavigation

val NavModule: Module
    get() = module {
        singleOf(::RootNavigation)
        singleOf(::HomeNavigation)
        singleOf(::SnackbarManager)
    }