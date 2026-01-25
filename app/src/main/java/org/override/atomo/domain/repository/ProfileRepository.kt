package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Profile

interface ProfileRepository {
    fun getProfileFlow(userId: String): Flow<Profile?>
    suspend fun getProfile(userId: String): Profile?
    suspend fun getProfileByUsername(username: String): Profile?
    suspend fun syncProfile(userId: String): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Profile>
    suspend fun deleteProfile(userId: String): Result<Unit>
    suspend fun checkUsernameAvailability(username: String): Boolean
}
