/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.pay.presentation

import org.override.atomo.domain.model.Plan

/**
 * Represents the intent/actions that can be performed on the Pay/Subscription screen.
 */
sealed interface PayAction {
    /** Reload subscription and plan data. */
    data object LoadData : PayAction
    
    /** Select a plan for purchase/upgrade. */
    data class SelectPlan(val plan: Plan) : PayAction
    
    /** Confirm subscription to the selected plan. */
    data object ConfirmSubscription : PayAction
    
    /** Dismiss the confirmation dialog. */
    data object DismissDialog : PayAction
    
    /** Navigate back to the previous screen. */
    data object NavigateBack : PayAction
}