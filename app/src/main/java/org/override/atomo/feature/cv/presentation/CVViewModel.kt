package org.override.atomo.feature.cv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID


class CVViewModel(
    private val cvUseCases: CvUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CVState())
    val state = _state
        .onStart { loadData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CVState(),
        )

    fun onAction(action: CVAction) {
        when (action) {
            is CVAction.CreateCv -> createCv()
            is CVAction.DeleteCv -> deleteCv(action.id)
            is CVAction.OpenCv -> openCv(action.id)
            is CVAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
            
            // Editor Actions
            CVAction.ToggleEditMode -> toggleEditMode()
            is CVAction.UpdateEditingCv -> updateEditingCv(action.cv)
            CVAction.SaveCv -> saveCv()
            CVAction.CancelEdit -> cancelEdit()
            is CVAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            CVAction.Back -> handleBack()
        }
    }

    private fun handleBack() {
        if (_state.value.isEditing) {
            cancelEdit()
        } else if (_state.value.editingCv != null) {
            // Close detail view
            _state.update { it.copy(editingCv = null, isEditing = false) }
        } else {
            // Navigate back from root if needed
            // rootNavigation.back()
        }
    }

    private fun openCv(id: String) {
        val cv = _state.value.cvs.find { it.id == id } ?: return
        _state.update { 
            it.copy(
                editingCv = cv, 
                isEditing = false 
            ) 
        }
    }

    private fun toggleEditMode() {
        _state.update { state -> state.copy(isEditing = !state.isEditing) }
    }

    private fun updateEditingCv(cv: Cv) {
        _state.update { it.copy(editingCv = cv) }
    }

    private fun saveCv() {
        viewModelScope.launch {
            val cv = _state.value.editingCv ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            cvUseCases.updateCv(cv).onSuccess {
                _state.update { it.copy(isLoading = false, isEditing = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun cancelEdit() {
        val currentId = _state.value.editingCv?.id ?: return
        val original = _state.value.cvs.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingCv = original) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first() 
            
            if (userId == null) {
                 _state.update { it.copy(isLoading = false) }
                 return@launch
            }

            launch {
                cvUseCases.getCvs(userId).collect { cvs ->
                    _state.update { state -> 
                         val currentId = state.editingCv?.id
                        val updatedEditing = if (currentId != null && !state.isEditing) {
                             cvs.find { it.id == currentId } ?: state.editingCv
                        } else {
                             state.editingCv
                        }
                        
                        state.copy(cvs = cvs, editingCv = updatedEditing)
                    }
                    checkCreationLimit(userId)
                }
            }
        }
    }

    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.CV)
        _state.update {
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun createCv() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            _state.update { it.copy(isLoading = true) }

            val result = canCreateServiceUseCase(userId, ServiceType.CV)
            if (result !is CanCreateResult.Success) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val newCv = Cv(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "My New CV",
                professionalSummary = "Summarize your professional journey.",
                isVisible = true,
                templateId = "standard",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            
            cvUseCases.createCv(newCv).onSuccess {
                 _state.update { it.copy(editingCv = newCv, isEditing = true, isLoading = false) }
            }.onFailure { error ->
                 _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteCv(id: String) {
        viewModelScope.launch {
            cvUseCases.deleteCv(id)
            if (_state.value.editingCv?.id == id) {
                _state.update { it.copy(editingCv = null, isEditing = false) }
            }
        }
    }
}