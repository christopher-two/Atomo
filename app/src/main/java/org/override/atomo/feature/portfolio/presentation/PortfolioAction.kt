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
 * Represents the intent/actions that can be performed on the Portfolio screen.
 */
sealed interface PortfolioAction {
    /** Create a new Portfolio. */
    data object CreatePortfolio : PortfolioAction
    
    /** Delete a Portfolio by its ID. */
    data class DeletePortfolio(val id: String) : PortfolioAction
    
    /** Open a Portfolio for viewing/editing. */
    data class OpenPortfolio(val id: String) : PortfolioAction
    
    /** Navigate to upgrade plan screen. */
    data object UpgradePlan : PortfolioAction
    
    // Editor Actions
    /** Toggle between view and edit modes. */
    data object ToggleEditMode : PortfolioAction
    
    /** Update the Portfolio currently being edited in the state buffer. */
    data class UpdateEditingPortfolio(val portfolio: Portfolio) : PortfolioAction
    
    /** Save changes to the current Portfolio. */
    data object SavePortfolio : PortfolioAction
    
    /** Cancel current edits and revert to original state. */
    data object CancelEdit : PortfolioAction
    
    /** Show or hide the preview bottom sheet. */
    data class TogglePreviewSheet(val show: Boolean) : PortfolioAction
    
    /** Navigate back or close current view. */
    data object Back : PortfolioAction
}