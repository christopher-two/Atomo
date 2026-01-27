package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuRoot
import org.override.atomo.feature.digital_menu.presentation.DigitalMenuViewModel

val DigitalMenuModule: Module
    get() = module {
        viewModelOf(::DigitalMenuViewModel)

        navigation<RouteMain.DigitalMenu> {
            DigitalMenuRoot(
                viewModel = koinViewModel()
            )
        }
    }
