/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation

/**
 * ViewModel for managing Profile feature state and business logic.
 * Handles profile loading, editing, validation (username check), and saving.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.util.AtomoUrlGenerator
import org.override.atomo.feature.profile.domain.ProfileValidator
import org.override.atomo.libs.session.api.SessionRepository

class ProfileViewModel(
    private val useCases: ProfileUseCases,
    private val sessionRepository: SessionRepository,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart { loadProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    /**
     * Gets the public "Link in Bio" URL for the current user.
     */
    fun getProfileUrl(): String? {
        val username = state.value.profile?.username
        return if (username != null) {
            AtomoUrlGenerator.generateProfileUrl(username)
        } else {
            null
        }
    }

    private var checkUsernameJob: Job? = null

    /**
     * Processes user intents/actions.
     *
     * @param action The action to perform.
     */
    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Refresh -> forceRefreshProfile()
            ProfileAction.EnterEditMode -> {
                val profile = state.value.profile ?: return
                _state.update { it.copy(
                    isEditing = true,
                    editUsername = profile.username,
                    editDisplayName = profile.displayName.orEmpty(),
                    editSocialLinks = profile.socialLinks ?: emptyMap(),
                    usernameError = null,
                    isUsernameAvailable = true
                ) }
            }
            ProfileAction.CancelEdit -> {
                _state.update { it.copy(isEditing = false, error = null) }
            }
            ProfileAction.SaveProfile -> saveProfile()
            is ProfileAction.UpdateUsername -> updateUsername(action.username)
            is ProfileAction.UpdateDisplayName -> {
                _state.update { it.copy(editDisplayName = action.displayName) }
            }
            is ProfileAction.UpdateSocialLink -> {
                val currentLinks = _state.value.editSocialLinks.toMutableMap()
                if (action.url.isBlank()) {
                    currentLinks.remove(action.platform)
                } else {
                    currentLinks[action.platform] = action.url
                }
                _state.update { it.copy(editSocialLinks = currentLinks) }
            }
            is ProfileAction.FormatSocialLink -> {
                val currentLinks = _state.value.editSocialLinks.toMutableMap()
                val currentUrl = currentLinks[action.platform] ?: return
                
                val formatted = formatUrl(action.platform, currentUrl)
                
                if (formatted != currentUrl) {
                     currentLinks[action.platform] = formatted
                     _state.update { it.copy(editSocialLinks = currentLinks) }
                }
            }
        }
    }
    
    private fun forceRefreshProfile() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            _state.update { it.copy(isLoading = true) }
            useCases.syncProfile(userId)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            _state.update { it.copy(isLoading = true) }
            useCases.getProfile(userId).collect { profile ->
                _state.update { it.copy(isLoading = false, profile = profile) }
                if (profile == null) {
                    // Try to sync if not found locally
                    syncProfile(userId)
                }
            }
        }
    }
    
    private fun syncProfile(userId: String) {
         viewModelScope.launch {
             useCases.syncProfile(userId)
                 .onFailure { e ->
                     snackbarManager.showMessage(e.message ?: "Sync error")
                 }
         }
    }

    private fun updateUsername(username: String) {
        val formatted = ProfileValidator.formatUsername(username)
        // Basic validation
        if (!ProfileValidator.isValidUsername(formatted)) {
            _state.update { it.copy(
                editUsername = formatted,
                usernameError = "Invalid characters. Use lowercase letters, numbers, _, -",
                isUsernameAvailable = false
            ) }
            return
        }

        _state.update { it.copy(
            editUsername = formatted, 
            usernameError = null,
            isCheckingUsername = true
        ) }

        // Debounce check
        checkUsernameJob?.cancel()
        checkUsernameJob = viewModelScope.launch {
            delay(500) // Debounce
            if (formatted == state.value.profile?.username) {
                 _state.update { it.copy(isCheckingUsername = false, isUsernameAvailable = true, usernameError = null) }
                 return@launch
            }
            
            val isAvailable = useCases.checkUsernameAvailability(formatted)
            _state.update { it.copy(
                isCheckingUsername = false,
                isUsernameAvailable = isAvailable,
                usernameError = if (isAvailable) null else "Username already taken"
            ) }
        }
    }

    private fun saveProfile() {
        if (!state.value.isUsernameAvailable || state.value.usernameError != null) return
        
        val currentState = state.value
        val currentProfile = currentState.profile ?: return
        
        // Format all links before saving to ensure consistency
        val formattedLinks = currentState.editSocialLinks.mapValues { (platform, url) ->
            formatUrl(platform, url)
        }.filterValues { it.isNotBlank() }

        val updatedProfile = currentProfile.copy(
            username = currentState.editUsername,
            displayName = currentState.editDisplayName.ifBlank { null },
            socialLinks = formattedLinks.ifEmpty { null },
            updatedAt = System.currentTimeMillis()
        )
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            useCases.updateProfile(updatedProfile)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isEditing = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
    
    private fun formatUrl(platform: String, input: String): String {
        if (input.isBlank()) return ""
        
        // If it looks like a username (no dots, no slashes), prepend base URL
        val isUsername = !input.contains(".") && !input.contains("/")
        if (isUsername) {
            val baseUrl = when(platform.lowercase()) {
                "instagram" -> "https://instagram.com/"
                "twitter", "x" -> "https://x.com/"
                "linkedin" -> "https://linkedin.com/in/"
                "github" -> "https://github.com/"
                "facebook" -> "https://facebook.com/"
                "tiktok" -> "https://tiktok.com/@"
                else -> ""
            }
            if (baseUrl.isNotEmpty()) {
                return "$baseUrl$input"
            }
        }
        
        // Use validator but ensure it has a scheme
        return if (ProfileValidator.isValidUrl(input) && input.contains(".")) {
             input
        } else {
             // Fallback: if it has dots but no scheme, add https
            if (!input.startsWith("http://") && !input.startsWith("https://") && input.contains(".")) {
                 "https://$input"
            } else {
                 input
            }
        }
    }
}