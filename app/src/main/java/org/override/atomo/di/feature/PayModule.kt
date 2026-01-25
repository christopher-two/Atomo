package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.pay.presentation.PayRoot
import org.override.atomo.feature.pay.presentation.PayViewModel

val PayModule: Module
    get() = module {
        viewModelOf(::PayViewModel)

        navigation<RouteMain.Pay> {
            PayRoot(
                viewModel = koinViewModel()
            )
        }
    }
