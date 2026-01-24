package org.override.atomo.feature.portfolio.presentation

data class PortfolioState(
    val isLoading: Boolean = false,
    val portfolios: List<org.override.atomo.domain.model.Portfolio> = emptyList(),
    val error: String? = null
)