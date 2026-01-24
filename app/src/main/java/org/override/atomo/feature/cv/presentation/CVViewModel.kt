package org.override.atomo.feature.cv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.usecase.cv.CvUseCases
import java.util.UUID

class CVViewModel(
    private val cvUseCases: CvUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(CVState())
    val state = _state
        .onStart { loadCvs() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CVState(),
        )

    fun onAction(action: CVAction) {
        when (action) {
            is CVAction.CreateCv -> createCv()
            is CVAction.DeleteCv -> deleteCv(action.id)
            is CVAction.OpenCv -> { /* Handle navigation to detail */ }
        }
    }

    private fun loadCvs() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // TODO: Get actual userId from session/auth
            val userId = "test_user_id" 
            cvUseCases.getCvs(userId).collect { cvs ->
                _state.update { it.copy(isLoading = false, cvs = cvs) }
            }
        }
    }

    private fun createCv() {
        viewModelScope.launch {
            val newCv = Cv(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id", // TODO: Real user
                title = "My New CV",
                professionalSummary = null,
                isVisible = true,
                templateId = "standard",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            cvUseCases.createCv(newCv)
        }
    }

    private fun deleteCv(id: String) {
        viewModelScope.launch {
            cvUseCases.deleteCv(id)
        }
    }
}