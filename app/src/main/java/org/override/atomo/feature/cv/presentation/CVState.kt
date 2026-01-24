package org.override.atomo.feature.cv.presentation

data class CVState(
    val isLoading: Boolean = false,
    val cvs: List<org.override.atomo.domain.model.Cv> = emptyList(),
    val error: String? = null
)