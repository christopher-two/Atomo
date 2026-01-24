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
import java.util.UUID

class InvitationViewModel(
    private val invitationUseCases: InvitationUseCases
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
        }
    }

    private fun loadInvitations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO
            invitationUseCases.getInvitations(userId).collect { list ->
                _state.update { it.copy(isLoading = false, invitations = list) }
            }
        }
    }

    private fun createInvitation() {
        viewModelScope.launch {
            val newInvitation = Invitation(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id",
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