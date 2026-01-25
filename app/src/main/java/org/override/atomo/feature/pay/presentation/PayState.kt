package org.override.atomo.feature.pay.presentation

data class PayState(
    val paramOne: String = "default",
    val paramTwo: List<String> = emptyList(),
)