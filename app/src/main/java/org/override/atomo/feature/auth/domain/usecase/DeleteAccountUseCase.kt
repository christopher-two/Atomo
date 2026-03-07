/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.domain.usecase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.override.atomo.data.local.AtomoDatabase
import org.override.atomo.feature.auth.domain.repository.GoogleAuthManager
import org.override.atomo.feature.profile.domain.repository.ProfileRepository
import org.override.atomo.feature.session.domain.repository.SessionRepository

class DeleteAccountUseCase(
    private val sessionRepository: SessionRepository,
    private val profileRepository: ProfileRepository,
    private val database: AtomoDatabase,
    private val googleAuthManager: GoogleAuthManager,
    private val supabase: SupabaseClient
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Get current userId
            val userId = sessionRepository.getCurrentUserId().firstOrNull()
            
            if (userId != null) {
                // 2. Delete remote profile and data (depends on backend cascade)
                profileRepository.deleteProfile(userId)
            }
            
            // 3. Delete user from auth.users via RPC
            if (userId != null) {
                supabase.postgrest.rpc("delete_user")
            }

            // 4. Clear local database
            database.clearAllTables()
            
            // 5. Sign out from Google / Credential Manager
            googleAuthManager.signOut()

            // 6. Clear session
            sessionRepository.clearUserSession()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
