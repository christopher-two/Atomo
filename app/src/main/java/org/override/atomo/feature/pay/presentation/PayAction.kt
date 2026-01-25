package org.override.atomo.feature.pay.presentation

import org.override.atomo.domain.model.Plan

sealed interface PayAction {
    data object LoadData : PayAction
    data class SelectPlan(val plan: Plan) : PayAction
    data object ConfirmSubscription : PayAction
    data object DismissDialog : PayAction
    data object NavigateBack : PayAction
}