package org.override.atomo.feature.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.onboarding.presentation.OnboardingAction
import org.override.atomo.feature.onboarding.presentation.OnboardingState
import org.override.atomo.feature.pay.presentation.components.PlanCard

@Composable
fun PlanSelectionStepContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isCurrent = state.selectedPlanId == plan.id,
                        onSubscribe = { onAction(OnboardingAction.SelectPlan(plan.id)) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onAction(OnboardingAction.PreviousStep) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Button(
                onClick = { onAction(OnboardingAction.NextStep) },
                enabled = state.canProceed
            ) {
                Text("Siguiente")
            }
        }
    }
}
