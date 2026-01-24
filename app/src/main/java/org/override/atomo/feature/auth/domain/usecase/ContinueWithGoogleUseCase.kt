package org.override.atomo.feature.auth.domain.usecase

import android.content.Context
import org.override.atomo.libs.auth.api.ExternalAuthResult
import org.override.atomo.libs.auth.api.GoogleAuthManager

class ContinueWithGoogleUseCase(
    private val repository: GoogleAuthManager,
    private val ctx: Context
) {
    suspend operator fun invoke(): Result<ExternalAuthResult> = repository.signIn(ctx)
}