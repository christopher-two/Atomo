/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.onboarding.presentation.OnboardingAction
import org.override.atomo.feature.onboarding.presentation.OnboardingState

/**
 * Content for step 1: Profile information.
 */
@Composable
fun ProfileStepContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Display Name
        OutlinedTextField(
            value = state.displayName,
            onValueChange = { onAction(OnboardingAction.UpdateDisplayName(it)) },
            label = { Text("Nombre para mostrar") },
            placeholder = { Text("Ej: Juan García") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        OutlinedTextField(
            value = state.username,
            onValueChange = { onAction(OnboardingAction.UpdateUsername(it)) },
            label = { Text("Username") },
            placeholder = { Text("Ej: juangarcia") },
            prefix = { Text("@") },
            singleLine = true,
            isError = state.usernameError != null,
            supportingText = {
                when {
                    state.usernameError != null -> Text(
                        text = state.usernameError,
                        color = MaterialTheme.colorScheme.error
                    )

                    state.isUsernameAvailable && state.username.isNotBlank() && !state.isCheckingUsername -> Text(
                        text = "Username disponible",
                        color = MaterialTheme.colorScheme.primary
                    )

                    else -> Text("Tu URL será: atomo.click/@${state.username}")
                }
            },
            trailingIcon = {
                when {
                    state.isCheckingUsername -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )

                    state.usernameError != null -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )

                    state.isUsernameAvailable && state.username.isNotBlank() -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Next button
        Button(
            onClick = { onAction(OnboardingAction.NextStep) },
            enabled = state.canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Siguiente")
        }
    }
}
