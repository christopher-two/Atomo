/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.invitation.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.core.ui.components.service.ColorPickerField
import org.override.atomo.core.ui.components.service.EditableSection
import org.override.atomo.core.ui.components.service.FontSelector
import org.override.atomo.core.ui.components.service.ServiceToolbar
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Invitation
import org.override.atomo.feature.invitation.presentation.components.InvitationShimmer

/**
 * Root composable for the Invitation feature.
 * Collects state from [InvitationViewModel] and passes it to the content.
 */
@Composable
fun InvitationRoot(
    viewModel: InvitationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    InvitationContent(
        state = state,
        onAction = viewModel::onAction
    )
}

/**
 * Main content composable for Invitation screen.
 * Handles switching between List view and Edit/Detail view.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationContent(
    state: InvitationState,
    onAction: (InvitationAction) -> Unit,
) {
    BackHandler(enabled = state.editingInvitation != null) {
        onAction(InvitationAction.Back)
    }

    // Preview Logic
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun buildPreviewJson(invitation: Invitation): String {
        return JSONObject().apply {
            put("eventName", invitation.eventName)
            put("eventDate", invitation.eventDate)
            put("location", invitation.location)
            put("description", invitation.description)
            put("primaryColor", invitation.primaryColor)
            put("fontFamily", invitation.fontFamily)
        }.toString()
    }

    fun updatePreview(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post { wv.evaluateJavascript("updatePreview($json)", null) }
        }
    }

    LaunchedEffect(state.editingInvitation) {
        state.editingInvitation?.let {
            delay(300)
            updatePreview(buildPreviewJson(it))
        }
    }

    if (state.editingInvitation == null) {
        InvitationListScreen(state, onAction)
    } else {
        val invitation = state.editingInvitation!!
        
        AtomoScaffold(
            topBar = {
                 TopAppBar(title = { Text(if (state.isEditing) "Edit Invitation" else invitation.eventName) })
            },
            floatingActionButton = {
                 ServiceToolbar(
                     isEditing = state.isEditing,
                     onBack = { onAction(InvitationAction.Back) },
                     onEditVerify = { 
                         if (state.isEditing) onAction(InvitationAction.SaveInvitation) 
                         else onAction(InvitationAction.ToggleEditMode) 
                     },
                     onPreview = { onAction(InvitationAction.TogglePreviewSheet(true)) },
                 )
            }
        ) { paddingValues ->
             Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                 // Event Info
                EditableSection(title = "Event Details", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = invitation.eventName,
                            onValueChange = { onAction(InvitationAction.UpdateEditingInvitation(invitation.copy(eventName = it))) },
                            label = { Text("Event Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AtomoTextField(
                            value = invitation.location ?: "",
                            onValueChange = { onAction(InvitationAction.UpdateEditingInvitation(invitation.copy(location = it))) },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                         // TODO: Replace with DatePicker
                        AtomoTextField(
                            value = invitation.eventDate?.toString() ?: "",
                            onValueChange = { /* Parse long? */ },
                            label = { Text("Date (Timestamp)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false // Disable manual edit of timestamp for now
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                             if (!invitation.location.isNullOrEmpty()) {
                                 Text("Location: ${invitation.location}", style = MaterialTheme.typography.bodyMedium)
                             }
                             if (invitation.eventDate != null) {
                                 Text("Date: ${invitation.eventDate}", style = MaterialTheme.typography.bodyMedium)
                             }
                        }
                    }
                }
                
                // Description
                EditableSection(title = "Message", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = invitation.description ?: "",
                            onValueChange = { onAction(InvitationAction.UpdateEditingInvitation(invitation.copy(description = it))) },
                            label = { Text("Invitation Message") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    } else {
                        Text(invitation.description ?: "No message", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                

                
                 Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    if (state.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(InvitationAction.TogglePreviewSheet(false)) },
            sheetState = sheetState
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                previewPageLoaded.value = true
                                state.editingInvitation?.let { updatePreview(buildPreviewJson(it)) }
                            }
                        }
                        loadUrl("https://atomo.click/preview/invitation")
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Composable that displays the list of user's Invitations.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@Composable
fun InvitationListScreen(state: InvitationState, onAction: (InvitationAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(InvitationAction.CreateInvitation) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Invitation")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.invitations.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                InvitationShimmer()
            }
        } else {
            if (state.invitations.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(InvitationAction.UpgradePlan) }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.invitations) { invitation ->
                        InvitationItem(invitation = invitation, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun InvitationItem(
    invitation: Invitation,
    onAction: (InvitationAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(InvitationAction.OpenInvitation(invitation.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = invitation.eventName,
                style = MaterialTheme.typography.titleLarge
            )
             Text(
                text = invitation.eventDate?.toString() ?: "No Date",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (invitation.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(InvitationAction.DeleteInvitation(invitation.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Invitation")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        InvitationContent(
            state = InvitationState(),
            onAction = {}
        )
    }
}