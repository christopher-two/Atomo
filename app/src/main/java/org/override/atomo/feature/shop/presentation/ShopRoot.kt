/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.shop.presentation

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.override.atomo.domain.model.Shop
import org.override.atomo.feature.shop.presentation.components.ShopShimmer

/**
 * Root composable for the Shop feature.
 * Collects state from [ShopViewModel] and passes it to the content.
 */
@Composable
fun ShopRoot(
    viewModel: ShopViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ShopContent(
        state = state,
        onAction = viewModel::onAction
    )
}

/**
 * Main content composable for Shop screen.
 * Handles switching between List view and Edit/Detail view.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopContent(
    state: ShopState,
    onAction: (ShopAction) -> Unit,
) {
    BackHandler(enabled = state.editingShop != null) {
        onAction(ShopAction.Back)
    }

    // Preview Logic
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCancelConfirmation by remember { mutableStateOf(false) }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text("Descartar cambios") },
            text = { Text("¿Estás seguro de que quieres salir sin guardar los cambios?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelConfirmation = false
                    onAction(ShopAction.CancelEdit)
                }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("Continuar editando")
                }
            }
        )
    }

    fun buildPreviewJson(shop: Shop): String {
        return JSONObject().apply {
            put("name", shop.name)
            put("description", shop.description)
            put("primaryColor", shop.primaryColor)
            put("fontFamily", shop.fontFamily)
        }.toString()
    }

    fun updatePreview(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post { wv.evaluateJavascript("updatePreview($json)", null) }
        }
    }

    LaunchedEffect(state.editingShop) {
        state.editingShop?.let {
            delay(300)
            updatePreview(buildPreviewJson(it))
        }
    }

    if (state.editingShop == null) {
        ShopListScreen(state, onAction)
    } else {
        val shop = state.editingShop!!
        
        AtomoScaffold(
            topBar = {
                 TopAppBar(title = { Text(if (state.isEditing) "Edit Shop" else shop.name) })
            },
            floatingActionButton = {
                 ServiceToolbar(
                     isEditing = state.isEditing,
                     saveEnabled = state.hasChanges,
                     onEditVerify = { 
                         if (state.isEditing) onAction(ShopAction.SaveShop) 
                         else onAction(ShopAction.ToggleEditMode) 
                     },
                     onCancel = {
                         if (state.hasChanges) {
                             showCancelConfirmation = true
                         } else {
                             onAction(ShopAction.CancelEdit)
                         }
                     },
                     onPreview = { onAction(ShopAction.TogglePreviewSheet(true)) },
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
                 // General Info
                EditableSection(title = "General Information", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = shop.name,
                            onValueChange = { onAction(ShopAction.UpdateEditingShop(shop.copy(name = it))) },
                            label = { Text("Shop Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AtomoTextField(
                            value = shop.description ?: "",
                            onValueChange = { onAction(ShopAction.UpdateEditingShop(shop.copy(description = it))) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    } else {
                        if (!shop.description.isNullOrEmpty()) {
                            Text(shop.description, style = MaterialTheme.typography.bodyMedium)
                        } else {
                             Text("No description provided.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                

                
                // Products Placeholder
                EditableSection(title = "Products", isEditing = state.isEditing) {
                    Text("Product management coming soon...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                
                 Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    if (state.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(ShopAction.TogglePreviewSheet(false)) },
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
                                state.editingShop?.let { updatePreview(buildPreviewJson(it)) }
                            }
                        }
                        loadUrl("https://atomo.click/preview/shop")
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Composable that displays the list of user's Shops.
 *
 * @param state Current UI state.
 * @param onAction Callback for user actions.
 */
@Composable
fun ShopListScreen(state: ShopState, onAction: (ShopAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(ShopAction.CreateShop) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Shop")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.shops.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                ShopShimmer()
            }
        } else {
            if (state.shops.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(ShopAction.UpgradePlan) }
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
                    items(state.shops) { shop ->
                        ShopItem(shop = shop, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItem(
    shop: Shop,
    onAction: (ShopAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(ShopAction.OpenShop(shop.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (shop.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(ShopAction.DeleteShop(shop.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Shop")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        ShopContent(
            state = ShopState(),
            onAction = {}
        )
    }
}