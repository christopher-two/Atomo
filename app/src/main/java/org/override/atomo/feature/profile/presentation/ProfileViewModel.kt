package org.override.atomo.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.usecase.profile.ProfileUseCases

class ProfileViewModel(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.UpdateDisplayName -> _state.update { it.copy(displayName = action.name) }
            is ProfileAction.SaveProfile -> saveProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO
            profileUseCases.getProfile(userId).collect { profile ->
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        profile = profile, 
                        displayName = profile?.displayName ?: ""
                    ) 
                }
            }
        }
    }

    private fun saveProfile() {
        val currentProfile = _state.value.profile ?: return
        val newDisplayName = _state.value.displayName
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val updatedProfile = currentProfile.copy(displayName = newDisplayName)
            profileUseCases.updateProfile(updatedProfile)
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure { error -> _state.update { it.copy(isLoading = false, error = error.message) } }
        }
    }
}