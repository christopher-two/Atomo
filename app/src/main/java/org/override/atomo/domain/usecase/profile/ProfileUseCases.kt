/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.repository.ProfileRepository

/**
 * Wrapper for Profile-related use cases.
 *
 * @property getProfile Retrieves the user's profile.
 * @property syncProfile Synchronizes the profile from the backend.
 * @property updateProfile Updates the user's profile.
 * @property checkUsernameAvailability Checks if a username is available.
 */
data class ProfileUseCases(
    val getProfile: GetProfileUseCase,
    val syncProfile: SyncProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val checkUsernameAvailability: CheckUsernameAvailabilityUseCase
)

/** Retrieves the user's profile data as a Flow. */
class GetProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(userId: String): Flow<Profile?> = repository.getProfileFlow(userId)
}

/** Synchronizes the user's profile from the server. */
class SyncProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): Result<Profile> = repository.syncProfile(userId)
}

/** Updates the user's profile. */
class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile): Result<Profile> = repository.updateProfile(profile)
}
