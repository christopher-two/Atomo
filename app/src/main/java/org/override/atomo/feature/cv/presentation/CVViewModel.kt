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
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.feature.home.presentation.ServiceType
import java.util.UUID


class CVViewModel(
    private val cvUseCases: CvUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase
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
            is CVAction.OpenCv -> { /* Handle navigation to detail */
            }

            is CVAction.UpgradePlan -> { /* Handle navigation to pay/subscription */
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO: Get actual userId from session/auth

            // Load CVs
            launch {
                cvUseCases.getCvs(userId).collect { cvs ->
                    _state.update { it.copy(cvs = cvs) }
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
            val userId = "test_user_id" // TODO: Real user

            // Re-check just in case
            val result = canCreateServiceUseCase(userId, ServiceType.CV)
            if (result !is CanCreateResult.Success) {
                return@launch
            }

            val newCv = Cv(
                id = UUID.randomUUID().toString(),
                userId = userId,
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