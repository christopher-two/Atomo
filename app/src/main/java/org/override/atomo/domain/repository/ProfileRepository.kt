/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Profile

/**
 * Repository interface for managing User Profiles.
 */
interface ProfileRepository {
    /** Retrieves the user's profile as a Flow. */
    fun getProfileFlow(userId: String): Flow<Profile?>

    /** Retrieves the user's profile (suspend). */
    suspend fun getProfile(userId: String): Profile?

    /** Retrieves a profile by username (public lookup). */
    suspend fun getProfileByUsername(username: String): Profile?

    /** Synchronizes the profile from the remote data source. */
    suspend fun syncProfile(userId: String): Result<Profile>

    /** Updates the user's profile. */
    suspend fun updateProfile(profile: Profile): Result<Profile>

    /** Deletes the user's profile (account deletion). */
    suspend fun deleteProfile(userId: String): Result<Unit>

    /** Checks if a username is available. */
    suspend fun checkUsernameAvailability(username: String): Boolean
}
