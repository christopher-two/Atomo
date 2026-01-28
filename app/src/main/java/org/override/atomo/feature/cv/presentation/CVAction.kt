package org.override.atomo.feature.cv.presentation

sealed interface CVAction {
    data object CreateCv : CVAction
    data class DeleteCv(val id: String) : CVAction
    data class OpenCv(val id: String) : CVAction
    data object UpgradePlan : CVAction
    
    // Editor Actions
    data object ToggleEditMode : CVAction
    data class UpdateEditingCv(val cv: org.override.atomo.domain.model.Cv) : CVAction
    data object SaveCv : CVAction
    data object CancelEdit : CVAction
    data class TogglePreviewSheet(val show: Boolean) : CVAction
    data object Back : CVAction
}
