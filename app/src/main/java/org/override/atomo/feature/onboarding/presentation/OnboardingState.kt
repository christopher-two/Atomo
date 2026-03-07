/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import org.override.atomo.feature.digital_menu.domain.model.MenuTemplate
import org.override.atomo.feature.profile.domain.model.Profile
import org.override.atomo.feature.subscription.domain.model.Plan

data class DishInput(
    val name: String,
    val price: Double,
    val categoryName: String,
    val description: String = ""
)

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
 * @property serviceName The name for the new menu.
 * @property templates Loaded menu templates.
 * @property selectedTemplateId The ID of the chosen template.
 * @property categories List of temporary category names.
 * @property dishes List of temporary dish inputs.
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

    // Step 2: Menu Details
    val serviceName: String = "",

    // Step 3: Template Selection
    val templates: List<MenuTemplate> = emptyList(),
    val selectedTemplateId: String? = null,

    // Step 4: Menu Items
    val categories: List<String> = emptyList(),
    val dishes: List<DishInput> = emptyList(),

    // Step 5: Plan Selection
    val plans: List<Plan> = emptyList(),
    val selectedPlanId: String? = null,

    // Step 6: Review
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

            OnboardingStep.MENU_DETAILS -> serviceName.isNotBlank()
            OnboardingStep.TEMPLATE_SELECTION -> selectedTemplateId != null
            OnboardingStep.MENU_ITEMS -> true
            OnboardingStep.PLAN_SELECTION -> selectedPlanId != null
            OnboardingStep.REVIEW -> true
        }
}

enum class OnboardingStep {
    PROFILE,
    MENU_DETAILS,
    TEMPLATE_SELECTION,
    MENU_ITEMS,
    PLAN_SELECTION,
    REVIEW
}
