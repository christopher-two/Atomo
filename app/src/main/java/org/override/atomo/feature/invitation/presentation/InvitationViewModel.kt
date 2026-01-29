/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.invitation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.libs.session.api.SessionRepository
import java.util.UUID

/**
 * ViewModel for managing Invitation feature state and business logic.
 * Handles CRUD operations, state management, and navigation logic for Invitations.
 */
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

    /**
     * Processes user intents/actions.
     *
     * @param action The action to perform.
     */
    fun onAction(action: InvitationAction) {
        when (action) {
            is InvitationAction.CreateInvitation -> createInvitation()
            is InvitationAction.DeleteInvitation -> deleteInvitation(action.id)
            is InvitationAction.OpenInvitation -> openInvitation(action.id)
            is InvitationAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
            
            // Editor Actions
            InvitationAction.ToggleEditMode -> toggleEditMode()
            is InvitationAction.UpdateEditingInvitation -> updateEditingInvitation(action.invitation)
            InvitationAction.SaveInvitation -> saveInvitation()
            InvitationAction.CancelEdit -> cancelEdit()
            is InvitationAction.TogglePreviewSheet -> _state.update { it.copy(showPreviewSheet = action.show) }
            InvitationAction.Back -> handleBack()
        }
    }

    private fun handleBack() {
        if (_state.value.isEditing) {
            cancelEdit()
        } else if (_state.value.editingInvitation != null) {
            // Close detail view
            _state.update { it.copy(editingInvitation = null, isEditing = false) }
        } else {
            // Navigate back from root if needed
        }
    }

    private fun openInvitation(id: String) {
        val invitation = _state.value.invitations.find { it.id == id } ?: return
        _state.update { 
            it.copy(
                editingInvitation = invitation, 
                isEditing = false 
            ) 
        }
    }

    private fun toggleEditMode() {
        _state.update { state -> state.copy(isEditing = !state.isEditing, hasChanges = false) }
    }

    private fun updateEditingInvitation(invitation: Invitation) {
        val original = _state.value.invitations.find { it.id == invitation.id }
        val hasChanges = invitation != original
        _state.update { it.copy(editingInvitation = invitation, hasChanges = hasChanges) }
    }

    private fun saveInvitation() {
        viewModelScope.launch {
            val invitation = _state.value.editingInvitation ?: return@launch
            _state.update { it.copy(isLoading = true) }
            
            invitationUseCases.updateInvitation(invitation).onSuccess {
                _state.update { it.copy(isLoading = false, isEditing = false, hasChanges = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun cancelEdit() {
        val currentId = _state.value.editingInvitation?.id ?: return
        val original = _state.value.invitations.find { it.id == currentId }
        _state.update { it.copy(isEditing = false, editingInvitation = original, hasChanges = false) }
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
                    _state.update { state -> 
                         val currentId = state.editingInvitation?.id
                        val updatedEditing = if (currentId != null && !state.isEditing) {
                             list.find { it.id == currentId } ?: state.editingInvitation
                        } else {
                             state.editingInvitation
                        }
                        
                        state.copy(invitations = list, editingInvitation = updatedEditing)
                    }
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
            _state.update { it.copy(isLoading = true) }
            
            val result = canCreateServiceUseCase(userId, ServiceType.INVITATION)
            if (result !is CanCreateResult.Success) {
                 _state.update { it.copy(isLoading = false) }
                return@launch
            }
            
            val newInvitation = Invitation(
                id = UUID.randomUUID().toString(),
                userId = userId,
                eventName = "My Event",
                eventDate = System.currentTimeMillis() + 86400000,
                location = "TBD",
                description = "You are invited!",
                isActive = true,
                templateId = "classic",
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            
            invitationUseCases.createInvitation(newInvitation).onSuccess {
                 _state.update { it.copy(editingInvitation = newInvitation, isEditing = true, isLoading = false) }
            }.onFailure { error ->
                 _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun deleteInvitation(id: String) {
        viewModelScope.launch {
            invitationUseCases.deleteInvitation(id)
            if (_state.value.editingInvitation?.id == id) {
                _state.update { it.copy(editingInvitation = null, isEditing = false) }
            }
        }
    }
}