package org.override.atomo.di.feature

import org.koin.core.module.Module

val FeaturesModule: List<Module>
    get() = listOf(
        AuthModule,
        HomeModule,
        MenuModule,
        ProfileModule,
        SettingsModule
    )