/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.onboarding.presentation.components.ProfileStepContent
import org.override.atomo.feature.onboarding.presentation.components.ReviewStepContent
import org.override.atomo.feature.onboarding.presentation.components.ServiceStepContent

@Composable
fun OnboardingRoot(
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
) {
    val progress = when (state.step) {
        OnboardingStep.PROFILE -> 0.33f
        OnboardingStep.SERVICE -> 0.66f
        OnboardingStep.REVIEW -> 1f
    }

    Scaffold(
        modifier = Modifier.imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step title
            Text(
                text = when (state.step) {
                    OnboardingStep.PROFILE -> "Completa tu perfil"
                    OnboardingStep.SERVICE -> "Crea tu primer servicio"
                    OnboardingStep.REVIEW -> "Revisa y confirma"
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (state.step) {
                    OnboardingStep.PROFILE -> "Cuéntanos un poco sobre ti"
                    OnboardingStep.SERVICE -> "Elige el tipo de servicio que quieres crear"
                    OnboardingStep.REVIEW -> "Verifica que todo esté correcto"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated step content
            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
                label = "OnboardingStep",
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) { step ->
                when (step) {
                    OnboardingStep.PROFILE -> ProfileStepContent(
                        state = state,
                        onAction = onAction
                    )

                    OnboardingStep.SERVICE -> ServiceStepContent(
                        state = state,
                        onAction = onAction
                    )

                    OnboardingStep.REVIEW -> ReviewStepContent(
                        state = state,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    AtomoTheme {
        OnboardingScreen(
            state = OnboardingState(),
            onAction = {}
        )
    }
}
