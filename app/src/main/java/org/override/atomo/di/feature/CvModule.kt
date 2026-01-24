package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.cv.presentation.CVRoot
import org.override.atomo.feature.cv.presentation.CVViewModel

val CvModule: Module
    get() = module {
        viewModelOf(::CVViewModel)

        navigation<RouteApp.CreateCV> {
            CVRoot(
                viewModel = koinViewModel()
            )
        }
    }
