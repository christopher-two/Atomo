package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.CvEducationEntity
import org.override.atomo.data.local.entity.CvEntity
import org.override.atomo.data.local.entity.CvExperienceEntity
import org.override.atomo.data.local.entity.CvSkillEntity

@Dao
interface CvDao {
    
    // CV operations
    @Query("SELECT * FROM cvs WHERE userId = :userId ORDER BY createdAt DESC")
    fun getCvsFlow(userId: String): Flow<List<CvEntity>>
    
    @Query("SELECT * FROM cvs WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getCvs(userId: String): List<CvEntity>
    
    @Query("SELECT * FROM cvs WHERE id = :cvId")
    suspend fun getCv(cvId: String): CvEntity?
    
    @Query("SELECT * FROM cvs WHERE id = :cvId")
    fun getCvFlow(cvId: String): Flow<CvEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCv(cv: CvEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCvs(cvs: List<CvEntity>)
    
    @Update
    suspend fun updateCv(cv: CvEntity)
    
    @Delete
    suspend fun deleteCv(cv: CvEntity)
    
    @Query("DELETE FROM cvs WHERE id = :cvId")
    suspend fun deleteCvById(cvId: String)
    
    // Education operations
    @Query("SELECT * FROM cv_education WHERE cvId = :cvId ORDER BY sortOrder ASC")
    fun getEducationFlow(cvId: String): Flow<List<CvEducationEntity>>
    
    @Query("SELECT * FROM cv_education WHERE cvId = :cvId ORDER BY sortOrder ASC")
    suspend fun getEducation(cvId: String): List<CvEducationEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducation(education: CvEducationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducationList(education: List<CvEducationEntity>)
    
    @Update
    suspend fun updateEducation(education: CvEducationEntity)
    
    @Delete
    suspend fun deleteEducation(education: CvEducationEntity)
    
    // Experience operations
    @Query("SELECT * FROM cv_experience WHERE cvId = :cvId ORDER BY sortOrder ASC")
    fun getExperienceFlow(cvId: String): Flow<List<CvExperienceEntity>>
    
    @Query("SELECT * FROM cv_experience WHERE cvId = :cvId ORDER BY sortOrder ASC")
    suspend fun getExperience(cvId: String): List<CvExperienceEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperience(experience: CvExperienceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperienceList(experience: List<CvExperienceEntity>)
    
    @Update
    suspend fun updateExperience(experience: CvExperienceEntity)
    
    @Delete
    suspend fun deleteExperience(experience: CvExperienceEntity)
    
    // Skills operations
    @Query("SELECT * FROM cv_skills WHERE cvId = :cvId ORDER BY sortOrder ASC")
    fun getSkillsFlow(cvId: String): Flow<List<CvSkillEntity>>
    
    @Query("SELECT * FROM cv_skills WHERE cvId = :cvId ORDER BY sortOrder ASC")
    suspend fun getSkills(cvId: String): List<CvSkillEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: CvSkillEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<CvSkillEntity>)
    
    @Update
    suspend fun updateSkill(skill: CvSkillEntity)
    
    @Delete
    suspend fun deleteSkill(skill: CvSkillEntity)
    
    @Query("DELETE FROM cv_education WHERE cvId = :cvId")
    suspend fun deleteEducationByCvId(cvId: String)
    
    @Query("DELETE FROM cv_experience WHERE cvId = :cvId")
    suspend fun deleteExperienceByCvId(cvId: String)
    
    @Query("DELETE FROM cv_skills WHERE cvId = :cvId")
    suspend fun deleteSkillsByCvId(cvId: String)
    
    @Query("DELETE FROM cvs WHERE userId = :userId")
    suspend fun deleteAllCvsByUser(userId: String)
}
