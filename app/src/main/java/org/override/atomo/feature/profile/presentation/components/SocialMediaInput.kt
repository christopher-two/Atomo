/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.override.atomo.feature.profile.presentation.ProfileAction

@Composable
fun SocialMediaInput(
    platform: String,
    value: String,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // We update local state immediately for responsiveness, but sync with 'value' from state
    var rawValue by remember(value) { mutableStateOf(value) }

    // If external value changes (e.g. formatting happened), update local state
    LaunchedEffect(value) {
        rawValue = value
    }

    OutlinedTextField(
        value = rawValue,
        onValueChange = {
            rawValue = it
            onAction(ProfileAction.UpdateSocialLink(platform, it))
        },
        label = { Text(platform.replaceFirstChar { it.uppercase() }) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onAction(ProfileAction.FormatSocialLink(platform))
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        placeholder = { Text("Username or URL") }
    )
}