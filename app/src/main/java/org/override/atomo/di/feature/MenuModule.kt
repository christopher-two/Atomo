@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.menu.presentation.MenuRoot
import org.override.atomo.feature.menu.presentation.MenuViewModel

val MenuModule: Module
    get() = module {
        viewModelOf(::MenuViewModel)
        navigation<RouteApp> {
            MenuRoot(
                viewModel = koinViewModel()
            )
        }
    }