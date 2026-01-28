package org.override.atomo.feature.cv.presentation

import org.override.atomo.domain.model.Cv

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