@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.auth.domain.usecase.SaveUserSessionUseCase
import org.override.atomo.feature.auth.presentation.AuthRoot
import org.override.atomo.feature.auth.presentation.viewmodel.AuthViewModel
import org.override.atomo.libs.auth.api.GoogleAuthManager
import org.override.atomo.libs.auth.impl.GoogleAuthManagerImpl

val AuthModule: Module
    get() = module {
        // Use cases
        factoryOf(::ContinueWithGoogleUseCase)
        factoryOf(::SaveUserSessionUseCase)
        
        // ViewModel
        viewModelOf(::AuthViewModel)
        
        // Navigation
        navigation<RouteApp.Auth> {
            AuthRoot(
                viewModel = koinViewModel()
            )
        }

        singleOf(::GoogleAuthManagerImpl) bind GoogleAuthManager::class
    }