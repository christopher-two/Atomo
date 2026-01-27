package org.override.atomo.feature.cv.presentation

sealed interface CVAction {
    data object CreateCv : CVAction
    data class DeleteCv(val id: String) : CVAction
    data class OpenCv(val id: String) : CVAction
    data object UpgradePlan : CVAction
}
