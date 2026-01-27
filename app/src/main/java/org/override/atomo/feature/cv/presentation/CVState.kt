package org.override.atomo.feature.cv.presentation

import org.override.atomo.domain.model.Cv

data class CVState(
    val isLoading: Boolean = false,
    val cvs: List<Cv> = emptyList(),
    val canCreate: Boolean = false,
    val limitReached: Boolean = false
)