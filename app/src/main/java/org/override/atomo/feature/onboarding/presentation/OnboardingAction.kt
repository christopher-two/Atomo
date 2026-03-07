/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

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

    // Social Links Step (Step 2)
    /** Update a social link value for a specific platform. */
    data class UpdateSocialLink(val platform: String, val url: String) : OnboardingAction

    /** Format (clean up) a social link when its field loses focus. */
    data class FormatSocialLink(val platform: String) : OnboardingAction

    // Menu Details Step (Step 3)
    /** Update the service name field. */
    data class UpdateServiceName(val name: String) : OnboardingAction

    // Template Selection Step (Step 3)
    /** Select a menu template. */
    data class SelectTemplate(val templateId: String) : OnboardingAction

    // Menu Items Step (Step 4)
    /** Add a new category. */
    data class AddCategory(val name: String) : OnboardingAction
    
    /** Remove a category. */
    data class RemoveCategory(val name: String) : OnboardingAction

    /** Add a new dish. */
    data class AddDish(val dish: DishInput) : OnboardingAction
    
    /** Remove a dish. */
    data class RemoveDish(val dish: DishInput) : OnboardingAction


    // Final
    /** Complete onboarding: save profile and create service. */
    data object FinishOnboarding : OnboardingAction

    /** Dismiss any error message. */
    data object DismissError : OnboardingAction
}
