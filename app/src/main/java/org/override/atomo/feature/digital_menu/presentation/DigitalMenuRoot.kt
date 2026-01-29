/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.digital_menu.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.core.ui.components.service.EditableSection
import org.override.atomo.core.ui.components.service.ServiceToolbar
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.components.CategoryDialog
import org.override.atomo.feature.digital_menu.presentation.components.DigitalMenuShimmer
import org.override.atomo.feature.digital_menu.presentation.components.DishDialog
import org.override.atomo.feature.digital_menu.presentation.components.DishItemRow

@Composable
fun DigitalMenuRoot(
    viewModel: DigitalMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DigitalMenuEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                DigitalMenuEvent.MenuSaved -> snackbarHostState.showSnackbar("Menu saved successfully")
            }
        }
    }

    AtomoScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (state.editingMenu != null) {
                ServiceToolbar(
                    isEditing = state.isEditing,
                    onEditVerify = {
                        if (state.isEditing) viewModel.onAction(DigitalMenuAction.SaveMenu)
                        else viewModel.onAction(DigitalMenuAction.ToggleEditMode)
                    },
                    onPreview = { viewModel.onAction(DigitalMenuAction.TogglePreviewSheet(true)) },
                    onDelete = { viewModel.onAction(DigitalMenuAction.ShowDeleteConfirmation) }
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
@Composable
fun DigitalMenuContent(
    state: DigitalMenuState,
    onAction: (DigitalMenuAction) -> Unit,
) {
    BackHandler(enabled = state.editingMenu != null) {
        onAction(DigitalMenuAction.Back)
    }

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

    if (state.editingMenu == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                DigitalMenuShimmer()
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You don't have a menu yet.", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (state.canCreate && !state.limitReached) {
                        Button(onClick = { onAction(DigitalMenuAction.CreateMenu) }) {
                            Text("Create Digital Menu")
                        }
                    } else if (state.limitReached) {
                        UpgradePlanScreen(onUpgradeClick = { onAction(DigitalMenuAction.UpgradePlan) })
                    }
                }
            }
        }
    } else {
        val menu = state.editingMenu
        val dishesByCategory = remember(menu.dishes, menu.categories) {
            val grouped = menu.dishes.groupBy { it.categoryId }
            menu.categories.map { it to (grouped[it.id] ?: emptyList()) } + (null to (grouped[null] ?: emptyList()))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isEditing) {
                EditableSection(title = "General Information", isEditing = true) {
                    AtomoTextField(
                        value = menu.name,
                        onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(name = it))) },
                        label = { Text("Menu Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AtomoTextField(
                        value = menu.description ?: "",
                        onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(description = it))) },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                EditableSection(
                    title = "Categories",
                    isEditing = true,
                    headerAction = {
                        TextButton(onClick = { onAction(DigitalMenuAction.OpenAddCategoryDialog) }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category")
                        }
                    }
                ) {
                    if (menu.categories.isEmpty()) {
                        Text("No categories yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            menu.categories.forEach { category ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(category.name, style = MaterialTheme.typography.bodyLarge)
                                    Row {
                                        TextButton(onClick = { onAction(DigitalMenuAction.OpenEditCategoryDialog(category)) }) { Text("Edit") }
                                        TextButton(onClick = { onAction(DigitalMenuAction.DeleteCategory(category)) }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                        }
                    }
                }

                EditableSection(
                    title = "Menu Items",
                    isEditing = true,
                    headerAction = {
                        Button(onClick = { onAction(DigitalMenuAction.OpenAddDishDialog) }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), modifier = Modifier.height(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Dish", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                ) {
                    if (menu.dishes.isEmpty()) {
                        Text("No items yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    } else {
                        dishesByCategory.forEach { (category, dishes) ->
                            if (dishes.isNotEmpty()) {
                                Text(category?.name ?: "Uncategorized", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    dishes.forEach { dish ->
                                        DishItemRow(dish = dish, isEditing = true, onEdit = { onAction(DigitalMenuAction.OpenEditDishDialog(dish)) }, onDelete = { onAction(DigitalMenuAction.DeleteDish(dish)) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Text(menu.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
                if (!menu.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(menu.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(32.dp))
                dishesByCategory.forEach { (category, dishes) ->
                    if (dishes.isNotEmpty()) {
                        Text(category?.name ?: "Other Items", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            dishes.forEach { dish ->
                                DishItemRow(dish = dish, isEditing = false, onEdit = {}, onDelete = {})
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    /** Dialogs */
    if (state.isDishDialogVisible) {
        DishDialog(
            dish = state.dishToEdit,
            categories = state.editingMenu?.categories ?: emptyList(),
            onDismiss = { onAction(DigitalMenuAction.CloseDishDialog) },
            onSave = { name, desc, price, img, catId -> onAction(DigitalMenuAction.SaveDish(name, desc, price, img, catId)) }
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
        ModalBottomSheet(onDismissRequest = { onAction(DigitalMenuAction.TogglePreviewSheet(false)) }, sheetState = sheetState) {
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
