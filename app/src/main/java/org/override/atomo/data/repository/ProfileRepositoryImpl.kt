package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.ProfileDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.ProfileDto
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val supabase: SupabaseClient
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
        val dto = profile.toDto()
        supabase.from("profiles")
            .upsert(dto)
        
        profileDao.insertProfile(profile.toEntity())
        profile
    }
    
    override suspend fun deleteProfile(userId: String): Result<Unit> = runCatching {
        supabase.from("profiles")
            .delete { filter { eq("id", userId) } }
        
        profileDao.deleteProfileById(userId)
    }
}
