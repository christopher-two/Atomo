/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.auth.domain.usecase

import android.content.Context
import org.override.atomo.libs.auth.api.ExternalAuthResult
import org.override.atomo.libs.auth.api.GoogleAuthManager

class ContinueWithGoogleUseCase(
    private val repository: GoogleAuthManager,
) {
    suspend operator fun invoke(ctx: Context): Result<ExternalAuthResult> = repository.signIn(ctx)
}