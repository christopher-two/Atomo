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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.core.ui.components.service.ColorPickerField
import org.override.atomo.core.ui.components.service.ColorPreview
import org.override.atomo.core.ui.components.service.EditableSection
import org.override.atomo.core.ui.components.service.FontPreview
import org.override.atomo.core.ui.components.service.FontSelector
import org.override.atomo.core.ui.components.service.ServiceToolbar
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.components.DigitalMenuShimmer
import java.text.NumberFormat
import java.util.Locale
import org.override.atomo.feature.digital_menu.presentation.components.DishDialog
import org.override.atomo.feature.digital_menu.presentation.components.DishItemRow
import org.override.atomo.feature.digital_menu.presentation.screens.DigitalMenuListScreen

@Composable
fun DigitalMenuRoot(
    viewModel: DigitalMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DigitalMenuContent(
        state = state,
        onAction = viewModel::onAction
    )
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

    /** Preview Logic */
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
        DigitalMenuListScreen(state, onAction)
    } else {
        val menu = state.editingMenu

        
        AtomoScaffold(
            topBar = {
                 TopAppBar(title = { Text(if (state.isEditing) "Edit Menu" else menu.name) })
            },
            floatingActionButton = {
                 ServiceToolbar(
                     isEditing = state.isEditing,
                     onBack = { onAction(DigitalMenuAction.Back) },
                     onEditVerify = { 
                         if (state.isEditing) onAction(DigitalMenuAction.SaveMenu) 
                         else onAction(DigitalMenuAction.ToggleEditMode) 
                     },
                     onPreview = { onAction(DigitalMenuAction.TogglePreviewSheet(true)) },
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
                 /** General Info */
                EditableSection(title = "General Information", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = menu.name,
                            onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(name = it))) },
                            label = { Text("Menu Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AtomoTextField(
                            value = menu.description ?: "",
                            onValueChange = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(description = it))) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    } else {
                        if (!menu.description.isNullOrEmpty()) {
                             Text(menu.description, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("No description provided.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                

                
                /** Menu Items (Dishes) */
                EditableSection(
                    title = "Menu Items", 
                    isEditing = state.isEditing,
                    headerAction = {
                         if (state.isEditing) {
                             Button(
                                 onClick = { onAction(DigitalMenuAction.OpenAddDishDialog) },
                                 contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                 modifier = Modifier.height(32.dp)
                             ) {
                                 Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                 Spacer(modifier = Modifier.width(4.dp))
                                 Text("Add Dish", style = MaterialTheme.typography.labelMedium)
                             }
                         }
                    }
                ) {
                    if (menu.dishes.isEmpty()) {
                        Text("No items yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            menu.dishes.forEach { dish ->
                                DishItemRow(
                                    dish = dish, 
                                    isEditing = state.isEditing,
                                    onEdit = { onAction(DigitalMenuAction.OpenEditDishDialog(dish)) },
                                    onDelete = { onAction(DigitalMenuAction.DeleteDish(dish)) }
                                )
                            }
                        }
                    }
                }
                
                 Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    /** Dish Dialog & Preview Sheet (Same as before) */
    if (state.isDishDialogVisible) {
        DishDialog(
            dish = state.dishToEdit,
            onDismiss = { onAction(DigitalMenuAction.CloseDishDialog) },
            onSave = { name, desc, price, img ->
                onAction(DigitalMenuAction.SaveDish(name, desc, price, img))
            }
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
}



@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        DigitalMenuContent(
            state = DigitalMenuState(),
            onAction = {}
        )
    }
}