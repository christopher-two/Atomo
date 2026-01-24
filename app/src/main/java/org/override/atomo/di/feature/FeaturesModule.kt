package org.override.atomo.di.feature

import org.koin.core.module.Module

val FeaturesModule: List<Module>
    get() = listOf(
        AuthModule,
        HomeModule,
        DashboardModule,
        ProfileModule,
        SettingsModule,
        CvModule,
        DigitalMenuModule,
        InvitationModule,
        PortfolioModule,
        ShopModule
    )