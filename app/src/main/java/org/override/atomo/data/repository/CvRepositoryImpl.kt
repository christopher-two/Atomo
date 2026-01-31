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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.override.atomo.data.local.dao.CvDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.CvDto
import org.override.atomo.data.remote.dto.CvEducationDto
import org.override.atomo.data.remote.dto.CvExperienceDto
import org.override.atomo.data.remote.dto.CvSkillDto
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill
import org.override.atomo.domain.repository.CvRepository

/**
 * Implementation of [CvRepository] that uses [CvDao] for local storage and [SupabaseClient] for remote synchronization.
 */
class CvRepositoryImpl(
    private val cvDao: CvDao,
    private val supabase: SupabaseClient
) : CvRepository {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getCvsFlow(userId: String): Flow<List<Cv>> {
        return cvDao.getCvsFlow(userId).flatMapLatest { cvEntities ->
            if (cvEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    cvEntities.map { cv ->
                        combine(
                            cvDao.getEducationFlow(cv.id),
                            cvDao.getExperienceFlow(cv.id),
                            cvDao.getSkillsFlow(cv.id)
                        ) { education, experience, skills ->
                            cv.toDomain().copy(
                                education = education.map { it.toDomain() },
                                experience = experience.map { it.toDomain() },
                                skills = skills.map { it.toDomain() }
                            )
                        }
                    }
                ) { it.toList() }
            }
        }
    }
    
    override suspend fun getCvs(userId: String): List<Cv> {
        return cvDao.getCvs(userId).map { it.toDomain() }
    }
    
    override suspend fun getCv(cvId: String): Cv? {
        val cv = cvDao.getCv(cvId)?.toDomain() ?: return null
        val education = cvDao.getEducation(cvId).map { it.toDomain() }
        val experience = cvDao.getExperience(cvId).map { it.toDomain() }
        val skills = cvDao.getSkills(cvId).map { it.toDomain() }
        return cv.copy(education = education, experience = experience, skills = skills)
    }
    
    override fun getCvFlow(cvId: String): Flow<Cv?> {
        return combine(
            cvDao.getCvFlow(cvId),
            cvDao.getEducationFlow(cvId),
            cvDao.getExperienceFlow(cvId),
            cvDao.getSkillsFlow(cvId)
        ) { cv, education, experience, skills ->
            cv?.toDomain()?.copy(
                education = education.map { it.toDomain() },
                experience = experience.map { it.toDomain() },
                skills = skills.map { it.toDomain() }
            )
        }
    }
    
    override suspend fun syncCvs(userId: String): Result<List<Cv>> = runCatching {
        val dtos = supabase.from("cvs")
            .select { filter { eq("user_id", userId) } }
            .decodeList<CvDto>()
        
        // Get current local CV IDs to detect deleted CVs
        val localCvIds = cvDao.getCvs(userId).map { it.id }.toSet()
        val remoteCvIds = dtos.map { it.id }.toSet()
        
        // Delete CVs that exist locally but not on server
        val deletedCvIds = localCvIds - remoteCvIds
        deletedCvIds.forEach { cvId ->
            cvDao.deleteEducationByCvId(cvId)
            cvDao.deleteExperienceByCvId(cvId)
            cvDao.deleteSkillsByCvId(cvId)
            cvDao.deleteCvById(cvId)
        }
        
        // Insert/update CVs from server
        val entities = dtos.map { it.toEntity() }
        cvDao.insertCvs(entities)
        
        // Sync sub-items for each CV (clear old data first)
        dtos.forEach { dto ->
            syncCvEducation(dto.id)
            syncCvExperience(dto.id)
            syncCvSkills(dto.id)
        }
        
        entities.map { it.toDomain() }
    }
    
    private suspend fun syncCvEducation(cvId: String) {
        // Clear old education for this CV
        cvDao.deleteEducationByCvId(cvId)
        
        val education = supabase.from("cv_education")
            .select { filter { eq("cv_id", cvId) } }
            .decodeList<CvEducationDto>()
        cvDao.insertEducationList(education.map { it.toEntity() })
    }
    
    private suspend fun syncCvExperience(cvId: String) {
        // Clear old experience for this CV
        cvDao.deleteExperienceByCvId(cvId)
        
        val experience = supabase.from("cv_experience")
            .select { filter { eq("cv_id", cvId) } }
            .decodeList<CvExperienceDto>()
        cvDao.insertExperienceList(experience.map { it.toEntity() })
    }
    
    private suspend fun syncCvSkills(cvId: String) {
        // Clear old skills for this CV
        cvDao.deleteSkillsByCvId(cvId)
        
        val skills = supabase.from("cv_skills")
            .select { filter { eq("cv_id", cvId) } }
            .decodeList<CvSkillDto>()
        cvDao.insertSkills(skills.map { it.toEntity() })
    }
    
    override suspend fun createCv(cv: Cv): Result<Cv> = runCatching {
        supabase.from("cvs").insert(cv.toDto())
        cvDao.insertCv(cv.toEntity())
        cv
    }
    
    override suspend fun updateCv(cv: Cv): Result<Cv> = runCatching {
        supabase.from("cvs").update(cv.toDto()) {
            filter { eq("id", cv.id) }
        }
        cvDao.updateCv(cv.toEntity())
        cv
    }
    
    override suspend fun deleteCv(cvId: String): Result<Unit> = runCatching {
        supabase.from("cvs").delete { filter { eq("id", cvId) } }
        cvDao.deleteCvById(cvId)
    }
    
    override suspend fun addEducation(education: CvEducation): Result<CvEducation> = runCatching {
        supabase.from("cv_education").insert(education)
        cvDao.insertEducation(education.toEntity())
        education
    }
    
    override suspend fun updateEducation(education: CvEducation): Result<CvEducation> = runCatching {
        supabase.from("cv_education").update(education) {
            filter { eq("id", education.id) }
        }
        cvDao.updateEducation(education.toEntity())
        education
    }
    
    override suspend fun deleteEducation(educationId: String): Result<Unit> = runCatching {
        supabase.from("cv_education").delete { filter { eq("id", educationId) } }
    }
    
    override suspend fun addExperience(experience: CvExperience): Result<CvExperience> = runCatching {
        supabase.from("cv_experience").insert(experience)
        cvDao.insertExperience(experience.toEntity())
        experience
    }
    
    override suspend fun updateExperience(experience: CvExperience): Result<CvExperience> = runCatching {
        supabase.from("cv_experience").update(experience) {
            filter { eq("id", experience.id) }
        }
        cvDao.updateExperience(experience.toEntity())
        experience
    }
    
    override suspend fun deleteExperience(experienceId: String): Result<Unit> = runCatching {
        supabase.from("cv_experience").delete { filter { eq("id", experienceId) } }
    }
    
    override suspend fun addSkill(skill: CvSkill): Result<CvSkill> = runCatching {
        supabase.from("cv_skills").insert(skill)
        cvDao.insertSkill(skill.toEntity())
        skill
    }
    
    override suspend fun updateSkill(skill: CvSkill): Result<CvSkill> = runCatching {
        supabase.from("cv_skills").update(skill) {
            filter { eq("id", skill.id) }
        }
        cvDao.updateSkill(skill.toEntity())
        skill
    }
    
    override suspend fun deleteSkill(skillId: String): Result<Unit> = runCatching {
        supabase.from("cv_skills").delete { filter { eq("id", skillId) } }
    }

    override suspend fun syncUp(userId: String): Result<Unit> = runCatching {
        // CvRepository syncs immediately on create/update operations
        // No optimistic updates with isSynced flag, so nothing to sync
    }
}
