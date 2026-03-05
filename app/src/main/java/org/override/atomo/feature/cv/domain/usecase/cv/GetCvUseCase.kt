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

/** Retrieves a single CV by its ID as a Flow. */
class GetCvUseCase(private val repository: CvRepository) {
    operator fun invoke(cvId: String): Flow<Cv?> = repository.getCvFlow(cvId)
}

