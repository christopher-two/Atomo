package org.override.atomo.feature.cv.presentation

import org.override.atomo.domain.model.Cv

sealed interface CVAction {
    data object CreateCv : CVAction
    data class DeleteCv(val id: String) : CVAction
    data class OpenCv(val id: String) : CVAction
    data object UpgradePlan : CVAction
    
    // Editor Actions
    data object ToggleEditMode : CVAction
    data class UpdateEditingCv(val cv: Cv) : CVAction
    data object SaveCv : CVAction
    data object CancelEdit : CVAction
    data class TogglePreviewSheet(val show: Boolean) : CVAction
    data object Back : CVAction
}
