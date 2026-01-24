@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.settings.presentation.SettingsRoot
import org.override.atomo.feature.settings.presentation.SettingsViewModel

val SettingsModule: Module
    get() = module {
        viewModelOf(::SettingsViewModel)
        navigation<RouteApp.Settings> {
            SettingsRoot(
                viewModel = koinViewModel()
            )
        }
    }