/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.shop.presentation.ShopRoot
import org.override.atomo.feature.shop.presentation.ShopViewModel

@OptIn(KoinExperimentalAPI::class)
val ShopModule: Module
    get() = module {
        viewModelOf(::ShopViewModel)

        navigation<RouteMain.Shop> {
            ShopRoot(
                viewModel = koinViewModel()
            )
        }
    }
