package org.override.atomo.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.repository.ProfileRepository

data class ProfileUseCases(
    val getProfile: GetProfileUseCase,
    val syncProfile: SyncProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val checkUsernameAvailability: CheckUsernameAvailabilityUseCase
)

class GetProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(userId: String): Flow<Profile?> = repository.getProfileFlow(userId)
}

class SyncProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): Result<Profile> = repository.syncProfile(userId)
}

class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile): Result<Profile> = repository.updateProfile(profile)
}
