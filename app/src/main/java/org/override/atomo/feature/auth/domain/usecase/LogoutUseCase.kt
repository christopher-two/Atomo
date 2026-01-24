package org.override.atomo.feature.auth.domain.usecase

import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.libs.auth.api.GoogleAuthManager
import org.override.atomo.libs.session.api.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogoutUseCase(
    private val sessionRepository: SessionRepository,
    private val database: AtomoDatabase,
    private val googleAuthManager: GoogleAuthManager
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Limpiar base de datos local (contenido del usuario)
            database.clearAllTables()
            
            // 2. Cerrar sesión en Google / Credential Manager
            googleAuthManager.signOut()

            // 3. Limpiar sesión (token/uid)
            sessionRepository.clearUserSession()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
