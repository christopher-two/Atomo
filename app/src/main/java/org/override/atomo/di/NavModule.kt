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