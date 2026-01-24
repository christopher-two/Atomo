@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.dashboard.presentation.DashboardRoot
import org.override.atomo.feature.dashboard.presentation.DashboardViewModel

val DashboardModule: Module
    get() = module {
        viewModelOf(::DashboardViewModel)
        navigation<RouteMain.Dashboard> {
            DashboardRoot(
                viewModel = koinViewModel()
            )
        }
    }