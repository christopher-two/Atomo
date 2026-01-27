package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.portfolio.presentation.PortfolioRoot
import org.override.atomo.feature.portfolio.presentation.PortfolioViewModel

val PortfolioModule: Module
    get() = module {
        viewModelOf(::PortfolioViewModel)

        navigation<RouteMain.Portfolio> {
            PortfolioRoot(
                viewModel = koinViewModel()
            )
        }
    }
