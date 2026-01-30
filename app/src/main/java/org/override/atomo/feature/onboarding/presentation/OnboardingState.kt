/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.model.ServiceType

/**
 * Represents the UI state for the Onboarding feature.
 *
 * @property step Current step in the onboarding wizard.
 * @property isLoading Whether a background operation is in progress.
 * @property error Error message if any operation failed.
 * @property displayName The display name being entered by the user.
 * @property username The username being entered by the user.
 * @property usernameError Validation error for username.
 * @property isCheckingUsername Whether username availability is being checked.
 * @property isUsernameAvailable Whether the current username is available.
 * @property selectedServiceType The type of service selected by the user.
 * @property serviceName The name for the new service.
 * @property profile The loaded user profile.
 */
data class OnboardingState(
    val step: OnboardingStep = OnboardingStep.PROFILE,
    val isLoading: Boolean = false,
    val error: String? = null,

    // Step 1: Profile
    val displayName: String = "",
    val username: String = "",
    val usernameError: String? = null,
    val isCheckingUsername: Boolean = false,
    val isUsernameAvailable: Boolean = true,

    // Step 2: Service Selection
    val selectedServiceType: ServiceType? = null,
    val serviceName: String = "",

    // Step 3: Review
    val profile: Profile? = null
) {
    /** Whether the current step can proceed to the next. */
    val canProceed: Boolean
        get() = when (step) {
            OnboardingStep.PROFILE -> displayName.isNotBlank() &&
                    username.isNotBlank() &&
                    isUsernameAvailable &&
                    usernameError == null &&
                    !isCheckingUsername

            OnboardingStep.SERVICE -> selectedServiceType != null && serviceName.isNotBlank()
            OnboardingStep.REVIEW -> true
        }
}

/** Steps in the onboarding wizard. */
enum class OnboardingStep {
    PROFILE,
    SERVICE,
    REVIEW
}
