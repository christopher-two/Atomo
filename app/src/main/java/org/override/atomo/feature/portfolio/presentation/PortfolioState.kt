package org.override.atomo.feature.portfolio.presentation

data class PortfolioState(
    val isLoading: Boolean = false,
    val portfolios: List<org.override.atomo.domain.model.Portfolio> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingPortfolio: org.override.atomo.domain.model.Portfolio? = null,
    val showPreviewSheet: Boolean = false
)