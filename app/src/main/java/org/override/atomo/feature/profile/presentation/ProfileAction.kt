package org.override.atomo.feature.profile.presentation

sealed interface ProfileAction {
    data class UpdateDisplayName(val name: String) : ProfileAction
    data object SaveProfile : ProfileAction
}