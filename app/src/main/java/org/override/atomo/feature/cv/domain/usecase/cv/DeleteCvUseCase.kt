/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.cv.domain.usecase.cv

import org.override.atomo.feature.cv.domain.repository.CvRepository

/** Deletes a CV by its ID. */
class DeleteCvUseCase(private val repository: CvRepository) {
    suspend operator fun invoke(cvId: String): Result<Unit> = repository.deleteCv(cvId)
}