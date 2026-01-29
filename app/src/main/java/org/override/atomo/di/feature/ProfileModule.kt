/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import okhttp3.Route
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.profile.presentation.ProfileRoot
import org.override.atomo.feature.profile.presentation.ProfileViewModel

val ProfileModule: Module
    get() = module {
        viewModelOf(::ProfileViewModel)
        navigation<RouteMain.Profile> {
            ProfileRoot(
                viewModel = koinViewModel()
            )
        }
    }