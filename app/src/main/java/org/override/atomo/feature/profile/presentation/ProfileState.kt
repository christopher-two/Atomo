package org.override.atomo.feature.profile.presentation

data class ProfileState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)