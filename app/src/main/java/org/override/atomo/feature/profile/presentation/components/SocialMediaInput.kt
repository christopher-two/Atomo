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

/**
 * Campo de texto genérico para una red social.
 * Acepta callbacks directos para que pueda reutilizarse fuera del perfil (p.ej. onboarding).
 */
@Composable
fun SocialMediaInput(
    platform: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusLost: () -> Unit = {}
) {
    var rawValue by remember(value) { mutableStateOf(value) }

    LaunchedEffect(value) {
        rawValue = value
    }

    OutlinedTextField(
        value = rawValue,
        onValueChange = { newValue ->
            rawValue = newValue
            onValueChange(newValue)
        },
        label = { Text(platform.replaceFirstChar { it.uppercase() }) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) onFocusLost()
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        placeholder = { Text("Username o URL") }
    )
}

/**
 * Sobrecarga de compatibilidad para [ProfileEditView] que sigue usando [ProfileAction].
 */
@Composable
fun SocialMediaInput(
    platform: String,
    value: String,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    SocialMediaInput(
        platform = platform,
        value = value,
        onValueChange = { onAction(ProfileAction.UpdateSocialLink(platform, it)) },
        modifier = modifier,
        onFocusLost = { onAction(ProfileAction.FormatSocialLink(platform)) }
    )
}