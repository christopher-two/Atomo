/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.profile.presentation.ProfileAction
import org.override.atomo.feature.profile.presentation.ProfileState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileEditView(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            VerticalFloatingToolbar(
                expanded = true,
                modifier = Modifier.padding(16.dp),
            ) {
                IconButton(
                    onClick = { onAction(ProfileAction.CancelEdit) }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Edit")
                }
                Crossfade(
                    targetState = state.isLoading
                ) { isLoading ->
                    if (isLoading) {
                        CircularWavyProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        IconButton(
                            onClick = { onAction(ProfileAction.SaveProfile) }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save Profile")
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize()
    ){ padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    padding.calculateLeftPadding(LocalLayoutDirection.current),
                )
                .consumeWindowInsets(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Info
            Text("Proflie Information", style = MaterialTheme.typography.titleLarge)
            
            OutlinedTextField(
                value = state.editDisplayName,
                onValueChange = { onAction(ProfileAction.UpdateDisplayName(it)) },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = state.editUsername,
                onValueChange = { onAction(ProfileAction.UpdateUsername(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.usernameError != null,
                supportingText = {
                    if (state.usernameError != null) {
                        Text(state.usernameError)
                    } else if (state.isCheckingUsername) {
                        Text("Checking availability...")
                    } else if (state.isUsernameAvailable) {
                        Text("Username available", color = MaterialTheme.colorScheme.primary)
                    }
                },
                trailingIcon = {
                    if (state.isCheckingUsername) {
                        CircularWavyProgressIndicator(modifier = Modifier.size(16.dp))
                    }
                }
            )
            
            HorizontalDivider()
            
            // Social Media
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Social Media", style = MaterialTheme.typography.titleLarge)
            }
            
            val platforms = listOf("instagram", "twitter", "linkedin", "github", "facebook", "tiktok")
            
            platforms.forEach { platform ->
                SocialMediaInput(
                    platform = platform,
                    value = state.editSocialLinks[platform] ?: "",
                    onAction = onAction
                )
            }
        }
    }
}
