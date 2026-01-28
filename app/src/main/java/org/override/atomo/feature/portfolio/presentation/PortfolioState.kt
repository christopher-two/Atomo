/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.portfolio.presentation

import org.override.atomo.domain.model.Portfolio

/**
 * Represents the UI state for the Portfolio feature.
 *
 * @property isLoading Whether a background operation is in progress.
 * @property portfolios The list of user's Portfolios.
 * @property error Error message if any operation failed.
 * @property canCreate Whether the user is allowed to create more Portfolios (plan limit).
 * @property limitReached Whether the plan limit for Portfolios has been reached.
 * @property isEditing Whether the UI is currently in edit mode.
 * @property editingPortfolio The temporary Portfolio object holding unsaved changes during editing.
 * @property showPreviewSheet Whether the preview bottom sheet is visible.
 */
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