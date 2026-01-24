package org.override.atomo.feature.auth.domain.usecase

import android.content.Context
import org.override.atomo.libs.auth.api.ExternalAuthResult
import org.override.atomo.libs.auth.api.GoogleAuthManager

class ContinueWithGoogleUseCase(
    private val repository: GoogleAuthManager,
) {
    suspend operator fun invoke(ctx: Context): Result<ExternalAuthResult> = repository.signIn(ctx)
}