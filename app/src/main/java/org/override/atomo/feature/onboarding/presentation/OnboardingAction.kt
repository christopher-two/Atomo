/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import org.override.atomo.domain.model.ServiceType

/**
 * Represents the actions that can be performed during onboarding.
 */
sealed interface OnboardingAction {
    // Navigation
    /** Move to the next step in the wizard. */
    data object NextStep : OnboardingAction

    /** Go back to the previous step. */
    data object PreviousStep : OnboardingAction

    // Profile Step (Step 1)
    /** Update the display name field. */
    data class UpdateDisplayName(val name: String) : OnboardingAction

    /** Update the username field (triggers validation). */
    data class UpdateUsername(val username: String) : OnboardingAction

    // Service Step (Step 2)
    /** Select a service type. */
    data class SelectServiceType(val type: ServiceType) : OnboardingAction

    /** Update the service name field. */
    data class UpdateServiceName(val name: String) : OnboardingAction

    // Final
    /** Complete onboarding: save profile and create service. */
    data object FinishOnboarding : OnboardingAction

    /** Dismiss any error message. */
    data object DismissError : OnboardingAction
}
