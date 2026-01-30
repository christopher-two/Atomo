/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count.EXACT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.ProfileDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.ProfileDto
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.repository.ProfileRepository

/**
 * Implementation of [ProfileRepository] using [ProfileDao] and [SupabaseClient].
 */
class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val supabase: SupabaseClient,
    private val syncManager: org.override.atomo.data.manager.SyncManager
) : ProfileRepository {

    
    override fun getProfileFlow(userId: String): Flow<Profile?> {
        return profileDao.getProfileFlow(userId).map { it?.toDomain() }
    }
    
    override suspend fun getProfile(userId: String): Profile? {
        return profileDao.getProfile(userId)?.toDomain()
    }
    
    override suspend fun getProfileByUsername(username: String): Profile? {
        return profileDao.getProfileByUsername(username)?.toDomain()
    }
    
    override suspend fun syncProfile(userId: String): Result<Profile> = runCatching {
        val dto = supabase.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingle<ProfileDto>()
        
        val entity = dto.toEntity()
        profileDao.insertProfile(entity)
        entity.toDomain()
    }
    
    override suspend fun updateProfile(profile: Profile): Result<Profile> = runCatching {
        // Optimistic update
        profileDao.updateProfile(profile.toEntity().copy(isSynced = false))

        syncManager.scheduleUpload(profile.id)
        
        profile
    }


    override suspend fun deleteProfile(userId: String): Result<Unit> = runCatching {
        supabase.from("profiles")
            .delete { filter { eq("id", userId) } }
        
        profileDao.deleteProfileById(userId)
    }

    override suspend fun checkUsernameAvailability(username: String): Boolean {
        return try {
            val count = supabase.from("profiles").select {
                count(EXACT)
                filter {
                    eq("username", username)
                }
            }.countOrNull() ?: 0
            count == 0L
        } catch (e: Exception) {
            false // Assume unavailable on error or handle differently if needed
        }
    }

    override suspend fun syncUp(userId: String): Result<Unit> = runCatching {
        val unsyncedProfile = profileDao.getUnsyncedProfile(userId)
        if (unsyncedProfile != null) {
            val dto = unsyncedProfile.toDomain().toDto()
            supabase.from("profiles").upsert(dto)
            profileDao.updateProfile(unsyncedProfile.copy(isSynced = true))
        }
    }
}

