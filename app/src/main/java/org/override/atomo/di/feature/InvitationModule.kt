package org.override.atomo.di.feature

import org.koin.androidx.compose.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.override.atomo.core.common.RouteApp
import org.override.atomo.core.common.RouteMain
import org.override.atomo.feature.invitation.presentation.InvitationRoot
import org.override.atomo.feature.invitation.presentation.InvitationViewModel

val InvitationModule: Module
    get() = module {
        viewModelOf(::InvitationViewModel)

        navigation<RouteMain.Invitation> {
            InvitationRoot(
                viewModel = koinViewModel()
            )
        }
    }
