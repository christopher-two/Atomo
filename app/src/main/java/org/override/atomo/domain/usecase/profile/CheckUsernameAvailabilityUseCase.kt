/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.profile

import org.override.atomo.domain.repository.ProfileRepository

/**
 * Checks if a proposed username is available (i.e., not already taken).
 */
class CheckUsernameAvailabilityUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(username: String): Boolean {
        return repository.checkUsernameAvailability(username)
    }
}
