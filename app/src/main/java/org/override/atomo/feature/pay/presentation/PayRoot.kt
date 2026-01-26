package org.override.atomo.feature.pay.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.ShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.feature.pay.presentation.components.PlanSection
import org.override.atomo.feature.pay.presentation.components.PayShimmer

@Composable
fun PayRoot(
    viewModel: PayViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PayScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PayScreen(
    state: PayState,
    onAction: (PayAction) -> Unit,
) {
    if (state.isLoading) {
        PayShimmer()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

        state.currentPlan?.let { plan ->
            Text(
                text = "Tu Plan Actual",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onBackground.copy(0.7f),
                letterSpacing = 2.sp
            )
            PlanSection(plan = plan)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!state.isLoading) {
            Text(
                text = "Mejora tu plan",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
        }

        state.plans.forEach { plan ->
            // Filter out current plan from options list
            if (plan.id != state.currentPlan?.id) {
                PlanSection(plan = plan)
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Confirmation Dialog
    if (state.showConfirmDialog && state.selectedPlan != null) {
        AlertDialog(
            containerColor = colorScheme.surface,
            onDismissRequest = { onAction(PayAction.DismissDialog) },
            title = { Text("Confirmar Suscripción") },
            text = {
                Text("¿Deseas cambiar al plan ${state.selectedPlan.name} por $${state.selectedPlan.price.toInt()} MXN/${state.selectedPlan.interval}?")
            },
            confirmButton = {
                Button(onClick = { onAction(PayAction.ConfirmSubscription) }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(PayAction.DismissDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
