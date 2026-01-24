package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill

interface CvRepository {
    fun getCvsFlow(userId: String): Flow<List<Cv>>
    suspend fun getCvs(userId: String): List<Cv>
    suspend fun getCv(cvId: String): Cv?
    fun getCvFlow(cvId: String): Flow<Cv?>
    suspend fun syncCvs(userId: String): Result<List<Cv>>
    suspend fun createCv(cv: Cv): Result<Cv>
    suspend fun updateCv(cv: Cv): Result<Cv>
    suspend fun deleteCv(cvId: String): Result<Unit>
    
    // Education operations
    suspend fun addEducation(education: CvEducation): Result<CvEducation>
    suspend fun updateEducation(education: CvEducation): Result<CvEducation>
    suspend fun deleteEducation(educationId: String): Result<Unit>
    
    // Experience operations
    suspend fun addExperience(experience: CvExperience): Result<CvExperience>
    suspend fun updateExperience(experience: CvExperience): Result<CvExperience>
    suspend fun deleteExperience(experienceId: String): Result<Unit>
    
    // Skill operations
    suspend fun addSkill(skill: CvSkill): Result<CvSkill>
    suspend fun updateSkill(skill: CvSkill): Result<CvSkill>
    suspend fun deleteSkill(skillId: String): Result<Unit>
}
