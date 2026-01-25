package org.override.atomo.domain.usecase.profile

import org.override.atomo.domain.repository.ProfileRepository

class CheckUsernameAvailabilityUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(username: String): Boolean {
        return repository.checkUsernameAvailability(username)
    }
}
