/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.profile.domain.model.Profile

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileDetailView(
    profile: Profile,
    onEditClick: () -> Unit,
    onSyncClick: () -> Unit,
    onShareClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = padding.calculateLeftPadding(LocalLayoutDirection.current))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatarSection(
                avatarUrl = profile.avatarUrl,
                displayName = profile.displayName
            )

            ProfileInfoSection(
                username = profile.username,
                displayName = profile.displayName,
                createdAt = profile.createdAt
            )

            ProfileActionsRow(
                onSyncClick = onSyncClick,
                onShareClick = onShareClick
            )

            profile.socialLinks
                ?.takeIf { it.isNotEmpty() }
                ?.let { links ->
                    ProfileSocialLinks(socialLinks = links)
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileDetailViewPreview() {
    ProfileDetailView(
        profile = Profile(
            id = "1",
            username = "chris_dev",
            displayName = "Christopher",
            avatarUrl = null,
            socialLinks = mapOf("github" to "https://github.com/chris_dev"),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ),
        onEditClick = {},
        onSyncClick = {},
        onShareClick = {},
        snackbarHostState = SnackbarHostState()
    )
}