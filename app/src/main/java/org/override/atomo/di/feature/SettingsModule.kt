@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.settings.domain.usecase.GetSettingsUseCase
import org.override.atomo.feature.settings.domain.usecase.SettingsUseCases
import org.override.atomo.feature.settings.domain.usecase.UpdateAppearanceUseCase
import org.override.atomo.feature.settings.domain.usecase.UpdateNotificationsUseCase
import org.override.atomo.feature.settings.domain.usecase.UpdatePrivacyUseCase
import org.override.atomo.feature.settings.presentation.SettingsRoot
import org.override.atomo.feature.settings.presentation.SettingsViewModel

val SettingsModule: Module
    get() = module {
        // Use Cases
        singleOf(::GetSettingsUseCase)
        singleOf(::UpdateAppearanceUseCase)
        singleOf(::UpdateNotificationsUseCase)
        singleOf(::UpdatePrivacyUseCase)
        singleOf(::SettingsUseCases)

        viewModelOf(::SettingsViewModel)
        navigation<RouteApp.Settings> {
            SettingsRoot(
                viewModel = koinViewModel()
            )
        }
    }