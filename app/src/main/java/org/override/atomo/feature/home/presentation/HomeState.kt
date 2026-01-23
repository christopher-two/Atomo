package org.override.atomo.feature.home.presentation

data class HomeState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)