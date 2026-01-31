/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation


import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.service.ServiceToolbar
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.components.CategoryDialog
import org.override.atomo.feature.digital_menu.presentation.components.DishDialog
import org.override.atomo.feature.digital_menu.presentation.sections.DigitalMenuEditor
import org.override.atomo.feature.digital_menu.presentation.sections.DigitalMenuEmptyState
import org.override.atomo.feature.digital_menu.presentation.sections.DigitalMenuViewer

@Composable
fun DigitalMenuRoot(
    viewModel: DigitalMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarManager = koinInject<SnackbarManager>()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DigitalMenuEvent.MenuSaved -> snackbarManager.showMessage("Menu saved successfully")
            }
        }
    }

    AtomoScaffold(
        snackbarHost = { SnackbarHost(snackbarManager.snackbarHostState) },
        floatingActionButton = {
            if (state.editingMenu != null) {
                ServiceToolbar(
                    isEditing = state.isEditing,
                    saveEnabled = state.hasUnsavedChanges,
                    onEditVerify = {
                        if (state.isEditing) viewModel.onAction(DigitalMenuAction.SaveMenu)
                        else viewModel.onAction(DigitalMenuAction.ToggleEditMode)
                    },
                    onCancel = { viewModel.onAction(DigitalMenuAction.CancelEdit) },
                    onPreview = { viewModel.onAction(DigitalMenuAction.TogglePreviewSheet(true)) },
                    onDelete = { viewModel.onAction(DigitalMenuAction.ShowDeleteConfirmation) },
                    additionalActions = {
                        if (!state.isEditing) {
                            IconButton(onClick = { viewModel.onAction(DigitalMenuAction.OpenAddDishDialog) }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Dish")
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            DigitalMenuContent(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DigitalMenuContent(
    state: DigitalMenuState,
    onAction: (DigitalMenuAction) -> Unit,
) {
    BackHandler(enabled = state.editingMenu != null) {
        onAction(DigitalMenuAction.Back)
    }

    // Preview Logic
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun buildPreviewJson(menu: Menu): String {
        return JSONObject().apply {
            put("name", menu.name)
            put("description", menu.description)
            put("primaryColor", menu.primaryColor)
            put("fontFamily", menu.fontFamily)
        }.toString()
    }

    fun updatePreview(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post { wv.evaluateJavascript("updatePreview($json)", null) }
        }
    }

    LaunchedEffect(state.editingMenu) {
        state.editingMenu?.let {
            delay(300)
            updatePreview(buildPreviewJson(it))
        }
    }

    when {
        state.editingMenu == null -> {
            DigitalMenuEmptyState(
                isLoading = state.isLoading,
                canCreate = state.canCreate,
                limitReached = state.limitReached,
                onAction = onAction
            )
        }

        state.isEditing -> {
            DigitalMenuEditor(
                menu = state.editingMenu,
                onAction = onAction
            )
        }

        else -> {
            DigitalMenuViewer(
                menu = state.editingMenu,
                onAction = onAction
            )
        }
    }

    // Dialogs
    if (state.isDishDialogVisible) {
        DishDialog(
            dish = state.dishToEdit,
            categories = state.editingMenu?.categories ?: emptyList(),
            onDismiss = { onAction(DigitalMenuAction.CloseDishDialog) },
            onSave = { name, desc, price, img, catId ->
                onAction(DigitalMenuAction.SaveDish(name, desc, price, img, catId))
            }
        )
    }

    if (state.isCategoryDialogVisible) {
        CategoryDialog(
            category = state.categoryToEdit,
            onDismiss = { onAction(DigitalMenuAction.CloseCategoryDialog) },
            onSave = { name -> onAction(DigitalMenuAction.SaveCategory(name)) }
        )
    }

    if (state.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(DigitalMenuAction.TogglePreviewSheet(false)) },
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
                                state.editingMenu?.let { updatePreview(buildPreviewJson(it)) }
                            }
                        }
                        loadUrl("https://atomo.click/preview/elegance")
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (state.isDiscardDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(DigitalMenuAction.HideDiscardConfirmation) },
            title = { Text("Descartar cambios") },
            text = { Text("¿Estás seguro de que quieres salir sin guardar los cambios?") },
            confirmButton = {
                TextButton(onClick = { onAction(DigitalMenuAction.ConfirmDiscard) }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(DigitalMenuAction.HideDiscardConfirmation) }) {
                    Text("Continuar editando")
                }
            }
        )
    }

    if (state.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { onAction(DigitalMenuAction.HideDeleteConfirmation) },
            title = { Text("Delete Menu") },
            text = { Text("Are you sure you want to delete this menu? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onAction(DigitalMenuAction.ConfirmDelete) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(DigitalMenuAction.HideDeleteConfirmation) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
