@file:OptIn(KoinExperimentalAPI::class)

package org.override.atomo.di.feature

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.create.presentation.CreateCVScreen
import org.override.atomo.feature.create.presentation.CreateDigitalMenuScreen
import org.override.atomo.feature.create.presentation.CreateInvitationScreen
import org.override.atomo.feature.create.presentation.CreatePortfolioScreen
import org.override.atomo.feature.create.presentation.CreateShopScreen

val CreateModule: Module
    get() = module {
        navigation<RouteApp.CreateDigitalMenu> {
            CreateDigitalMenuScreen()
        }
        
        navigation<RouteApp.CreatePortfolio> {
            CreatePortfolioScreen()
        }
        
        navigation<RouteApp.CreateCV> {
            CreateCVScreen()
        }
        
        navigation<RouteApp.CreateShop> {
            CreateShopScreen()
        }
        
        navigation<RouteApp.CreateInvitation> {
            CreateInvitationScreen()
        }
    }
