package org.override.atomo.feature.portfolio.presentation

import org.override.atomo.domain.model.Portfolio

data class PortfolioState(
    val isLoading: Boolean = false,
    val portfolios: List<Portfolio> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingPortfolio: Portfolio? = null,
    val showPreviewSheet: Boolean = false
)