package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.ProfileEntity

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profiles WHERE id = :userId")
    fun getProfileFlow(userId: String): Flow<ProfileEntity?>
    
    @Query("SELECT * FROM profiles WHERE id = :userId")
    suspend fun getProfile(userId: String): ProfileEntity?
    
    @Query("SELECT * FROM profiles WHERE username = :username")
    suspend fun getProfileByUsername(username: String): ProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    @Update
    suspend fun updateProfile(profile: ProfileEntity)
    
    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)
    
    @Query("DELETE FROM profiles WHERE id = :userId")
    suspend fun deleteProfileById(userId: String)
}
