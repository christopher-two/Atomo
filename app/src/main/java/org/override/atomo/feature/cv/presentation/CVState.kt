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
 * Represents the UI state for the CV feature.
 *
 * @property isLoading Whether a background operation is in progress.
 * @property cvs The list of user's CVs.
 * @property error Error message if any operation failed.
 * @property canCreate Whether the user is allowed to create more CVs (plan limit).
 * @property limitReached Whether the plan limit for CVs has been reached.
 * @property isEditing Whether the UI is currently in edit mode.
 * @property editingCv The temporary CV object holding unsaved changes during editing.
 * @property showPreviewSheet Whether the preview bottom sheet is visible.
 */
data class CVState(
    val isLoading: Boolean = false,
    val cvs: List<Cv> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingCv: Cv? = null,
    val showPreviewSheet: Boolean = false
)