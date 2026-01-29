/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.portfolio.presentation

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
import androidx.compose.ui.Alignment
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
import org.override.atomo.core.ui.components.service.ImagePicker // If needed
import org.override.atomo.core.ui.components.service.ServiceToolbar
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.feature.portfolio.presentation.components.PortfolioShimmer

/**
 * Root composable for the Portfolio feature.
 * Collects state from [PortfolioViewModel] and passes it to the content.
 */
@Composable
fun PortfolioRoot(
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PortfolioContent(
        state = state,
        onAction = viewModel::onAction
    )
}

/**
 * Main content composable for Portfolio screen.
 * Handles switching between List view and Edit/Detail view.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioContent(
    state: PortfolioState,
    onAction: (PortfolioAction) -> Unit,
) {
    // Back Handler to ensure we close edit mode/detail view
    BackHandler(enabled = state.editingPortfolio != null) {
        onAction(PortfolioAction.Back)
    }

    // Preview Logic (Copied/Adapted from DigitalMenu)
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun buildPreviewJson(portfolio: Portfolio): String {
        return JSONObject().apply {
            put("title", portfolio.title)
            put("description", portfolio.description)
            put("templateId", portfolio.templateId)
            put("primaryColor", portfolio.primaryColor)
            put("fontFamily", portfolio.fontFamily)
        }.toString()
    }

    fun updatePreview(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post { wv.evaluateJavascript("updatePreview($json)", null) }
        }
    }

    LaunchedEffect(state.editingPortfolio) {
        state.editingPortfolio?.let {
            delay(300)
            updatePreview(buildPreviewJson(it))
        }
    }

    // LIST VIEW
    if (state.editingPortfolio == null) {
        PortfolioListScreen(state, onAction)
    } 
    // DETAIL / EDIT VIEW
    else {
        val portfolio = state.editingPortfolio!!
        
        AtomoScaffold(
            topBar = {
                // We use floating toolbar instead, but if AtomoScaffold requires topBar, we can pass generic header
                // Or just empty
                 TopAppBar(title = { Text(if (state.isEditing) "Edit Portfolio" else portfolio.title) })
            },
            floatingActionButton = {
                 ServiceToolbar(
                     isEditing = state.isEditing,
                     onEditVerify = { 
                         if (state.isEditing) onAction(PortfolioAction.SavePortfolio) 
                         else onAction(PortfolioAction.ToggleEditMode) 
                     },
                     onPreview = { onAction(PortfolioAction.TogglePreviewSheet(true)) },
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
                // EDITABLE SECTIONS
                
                // General Info
                EditableSection(title = "General Information", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = portfolio.title,
                            onValueChange = { onAction(PortfolioAction.UpdateEditingPortfolio(portfolio.copy(title = it))) },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AtomoTextField(
                            value = portfolio.description ?: "",
                            onValueChange = { onAction(PortfolioAction.UpdateEditingPortfolio(portfolio.copy(description = it))) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    } else {
                        if (!portfolio.description.isNullOrEmpty()) {
                            Text(portfolio.description, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("No description provided.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                

                
                // Items Placeholder
                EditableSection(title = "Items", isEditing = state.isEditing) {
                    Text("Portfolio items management coming soon...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    // TODO: Add list of items here
                }
                
                 // Extra spacing for FAB
                Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    // Preview Sheet
    if (state.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(PortfolioAction.TogglePreviewSheet(false)) },
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
                                state.editingPortfolio?.let { updatePreview(buildPreviewJson(it)) }
                            }
                        }
                        loadUrl("https://atomo.click/preview/portfolio") // Verify correct URL
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Composable that displays the list of user's Portfolios.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@Composable
fun PortfolioListScreen(state: PortfolioState, onAction: (PortfolioAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(PortfolioAction.CreatePortfolio) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Portfolio")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.portfolios.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                PortfolioShimmer()
            }
        } else {
            if (state.portfolios.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(PortfolioAction.UpgradePlan) }
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
                    items(state.portfolios) { portfolio ->
                        PortfolioItem(portfolio = portfolio, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioItem(
    portfolio: Portfolio,
    onAction: (PortfolioAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(PortfolioAction.OpenPortfolio(portfolio.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = portfolio.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (portfolio.isVisible) "Visible" else "Hidden",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(PortfolioAction.DeletePortfolio(portfolio.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Portfolio")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        PortfolioContent(
            state = PortfolioState(),
            onAction = {}
        )
    }
}