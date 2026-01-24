package org.override.atomo.feature.auth.domain.usecase

import org.override.atomo.libs.session.api.SessionRepository

class SaveUserSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return sessionRepository.saveUserSession(userId)
    }
}
