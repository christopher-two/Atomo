/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.cv.presentation

import org.override.atomo.domain.model.Cv

/**
 * Represents the intent/actions that can be performed on the CV screen.
 */
sealed interface CVAction {
    /** Create a new CV. */
    data object CreateCv : CVAction
    
    /** Delete a CV by its ID. */
    data class DeleteCv(val id: String) : CVAction
    
    /** Open a CV for viewing/editing. */
    data class OpenCv(val id: String) : CVAction
    
    /** Navigate to upgrade plan screen. */
    data object UpgradePlan : CVAction
    
    // Editor Actions
    /** Toggle between view and edit modes. */
    data object ToggleEditMode : CVAction
    
    /** Update the CV currently being edited in the state buffer. */
    data class UpdateEditingCv(val cv: Cv) : CVAction
    
    /** Save changes to the current CV. */
    data object SaveCv : CVAction
    
    /** Cancel current edits and revert to original state. */
    data object CancelEdit : CVAction
    
    /** Show or hide the preview bottom sheet. */
    data class TogglePreviewSheet(val show: Boolean) : CVAction
    
    /** Navigate back or close current view. */
    data object Back : CVAction
}
