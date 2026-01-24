package org.override.atomo.domain.usecase.cv

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.CvEducation
import org.override.atomo.domain.model.CvExperience
import org.override.atomo.domain.model.CvSkill
import org.override.atomo.domain.repository.CvRepository

data class CvUseCases(
    val getCvs: GetCvsUseCase,
    val getCv: GetCvUseCase,
    val syncCvs: SyncCvsUseCase,
    val createCv: CreateCvUseCase,
    val updateCv: UpdateCvUseCase,
    val deleteCv: DeleteCvUseCase,
    val addEducation: AddEducationUseCase,
    val addExperience: AddExperienceUseCase,
    val addSkill: AddSkillUseCase
)

class GetCvsUseCase(private val repository: CvRepository) {
    operator fun invoke(userId: String): Flow<List<Cv>> = repository.getCvsFlow(userId)
}

class GetCvUseCase(private val repository: CvRepository) {
    operator fun invoke(cvId: String): Flow<Cv?> = repository.getCvFlow(cvId)
}

class SyncCvsUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(userId: String): Result<List<Cv>> = repository.syncCvs(userId)
}

class CreateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cv: Cv): Result<Cv> = repository.createCv(cv)
}

class UpdateCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cv: Cv): Result<Cv> = repository.updateCv(cv)
}

class DeleteCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cvId: String): Result<Unit> = repository.deleteCv(cvId)
}

class AddEducationUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(education: CvEducation): Result<CvEducation> = repository.addEducation(education)
}

class AddExperienceUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(experience: CvExperience): Result<CvExperience> = repository.addExperience(experience)
}

class AddSkillUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(skill: CvSkill): Result<CvSkill> = repository.addSkill(skill)
}
