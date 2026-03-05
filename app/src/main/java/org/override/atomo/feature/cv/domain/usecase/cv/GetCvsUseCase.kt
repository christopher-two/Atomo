/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.cv.domain.usecase.cv

import kotlinx.coroutines.flow.Flow
import org.override.atomo.feature.cv.domain.model.Cv
import org.override.atomo.feature.cv.domain.repository.CvRepository

/** Retrieves all CVs for a specific user as a Flow. */
class GetCvsUseCase(private val repository: CvRepository) {
    operator fun invoke(userId: String): Flow<List<Cv>> = repository.getCvsFlow(userId)
}