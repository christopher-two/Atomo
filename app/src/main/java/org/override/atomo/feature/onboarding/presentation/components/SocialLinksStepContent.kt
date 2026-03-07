/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.onboarding.presentation.OnboardingAction
import org.override.atomo.feature.onboarding.presentation.OnboardingState
import org.override.atomo.feature.profile.presentation.components.SocialMediaInput

private val SOCIAL_PLATFORMS = listOf(
    "instagram",
    "twitter",
    "linkedin",
    "github",
    "facebook",
    "tiktok"
)

/**
 * Content for step 2: Social links (optional).
 * Reutiliza [SocialMediaInput] del módulo de perfil.
 */
@Composable
fun SocialLinksStepContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Este paso es opcional. Puedes completarlo más tarde desde tu perfil.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        SOCIAL_PLATFORMS.forEach { platform ->
            SocialMediaInput(
                platform = platform,
                value = state.socialLinks[platform] ?: "",
                onValueChange = { onAction(OnboardingAction.UpdateSocialLink(platform, it)) },
                onFocusLost = { onAction(OnboardingAction.FormatSocialLink(platform)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onAction(OnboardingAction.PreviousStep) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar"
                )
            }

            Button(onClick = { onAction(OnboardingAction.NextStep) }) {
                Text("Continuar")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialLinksStepContentPreview() {
    AtomoTheme {
        SocialLinksStepContent(
            state = OnboardingState(
                socialLinks = mapOf("instagram" to "https://instagram.com/john")
            ),
            onAction = {}
        )
    }
}

