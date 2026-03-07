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
import org.override.atomo.feature.auth.data.repository.GoogleAuthManagerImpl
import org.override.atomo.feature.auth.domain.repository.GoogleAuthManager
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.auth.domain.usecase.DeleteAccountUseCase
import org.override.atomo.feature.auth.domain.usecase.LogoutUseCase
import org.override.atomo.feature.auth.domain.usecase.SaveUserSessionUseCase
import org.override.atomo.feature.auth.presentation.AuthRoot
import org.override.atomo.feature.auth.presentation.AuthViewModel

val AuthModule: Module
    get() = module {
        // Use cases
        factoryOf(::ContinueWithGoogleUseCase)
        factoryOf(::SaveUserSessionUseCase)
        factoryOf(::LogoutUseCase)
        factoryOf(::DeleteAccountUseCase)

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