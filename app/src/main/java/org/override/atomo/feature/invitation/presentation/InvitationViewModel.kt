package org.override.atomo.feature.invitation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.libs.session.api.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.UUID


class InvitationViewModel(
    private val invitationUseCases: InvitationUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InvitationState())
    val state = _state
        .onStart { loadInvitations() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = InvitationState(),
        )

    fun onAction(action: InvitationAction) {
        when (action) {
            is InvitationAction.CreateInvitation -> createInvitation()
            is InvitationAction.DeleteInvitation -> deleteInvitation(action.id)
            is InvitationAction.OpenInvitation -> { /* Handle navigation */ }
            is InvitationAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
        }
    }

    private fun loadInvitations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().first()
            
            if (userId == null) {
                // Handle not logged in or return
                _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            launch {
                 invitationUseCases.getInvitations(userId).collect { list ->
                    _state.update { it.copy(invitations = list) }
                    checkCreationLimit(userId)
                }
            }
        }
    }
    
    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.INVITATION)
        _state.update { 
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun createInvitation() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().first() ?: return@launch
            
            // Re-check just in case
            val result = canCreateServiceUseCase(userId, ServiceType.INVITATION)
            if (result !is CanCreateResult.Success) {
                return@launch
            }
            
            val newInvitation = Invitation(
                id = UUID.randomUUID().toString(),
                userId = userId,
                eventName = "My Event",
                eventDate = null,
                location = null,
                description = "Join us!",
                isActive = true,
                templateId = "elegant",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            invitationUseCases.createInvitation(newInvitation)
        }
    }

    private fun deleteInvitation(id: String) {
        viewModelScope.launch {
            invitationUseCases.deleteInvitation(id)
        }
    }
}