package org.override.atomo.feature.profile.presentation

sealed interface ProfileAction {
    data object EnterEditMode : ProfileAction
    data object CancelEdit : ProfileAction
    data object SaveProfile : ProfileAction
    data class UpdateUsername(val username: String) : ProfileAction
    data class UpdateDisplayName(val displayName: String) : ProfileAction
    data class UpdateSocialLink(val platform: String, val url: String) : ProfileAction
    data class FormatSocialLink(val platform: String) : ProfileAction
}