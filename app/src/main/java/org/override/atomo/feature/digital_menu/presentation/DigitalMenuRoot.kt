package org.override.atomo.feature.digital_menu.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Menu
import org.override.atomo.feature.digital_menu.presentation.components.DigitalMenuShimmer

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

    if (state.editingMenu == null) {
        DigitalMenuListScreen(state, onAction)
    } else {
        val menu = state.editingMenu!!
        
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
                 // General Info
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
                        Text(menu.name, style = MaterialTheme.typography.headlineSmall)
                        Text(menu.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                // Appearance
                EditableSection(title = "Appearance", isEditing = state.isEditing) {
                    if (state.isEditing) {
                         ColorPickerField(
                             selectedColor = menu.primaryColor,
                             onColorSelected = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(primaryColor = it))) }
                         )
                         FontSelector(
                             selectedFont = menu.fontFamily,
                             onFontSelected = { onAction(DigitalMenuAction.UpdateEditingMenu(menu.copy(fontFamily = it))) },
                             modifier = Modifier.padding(top = 16.dp)
                         )
                    } else {
                        Text("Primary Color: ${menu.primaryColor}")
                        Text("Font: ${menu.fontFamily}")
                    }
                }
                
                // Menu Items (Dishes)
                EditableSection(title = "Menu Items", isEditing = state.isEditing) {
                    if (menu.dishes.isEmpty()) {
                        Text("No items yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            menu.dishes.forEach { dish ->
                                AtomoCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(dish.name, fontWeight = FontWeight.Bold)
                                            Text("${dish.price}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        if (state.isEditing) {
                                            Row {
                                                IconButton(onClick = { onAction(DigitalMenuAction.OpenEditDishDialog(dish)) }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                                }
                                                IconButton(onClick = { onAction(DigitalMenuAction.DeleteDish(dish)) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if (state.isEditing) {
                        Button(
                            onClick = { onAction(DigitalMenuAction.OpenAddDishDialog) },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add Dish")
                        }
                    }
                }
                
                 Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    // Dish Dialog
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
                        loadUrl("https://atomo.click/preview/elegance") // Or menu specific
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun DishDialog(
    dish: Dish?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String?) -> Unit
) {
    var name by remember { mutableStateOf(dish?.name ?: "") }
    var description by remember { mutableStateOf(dish?.description ?: "") }
    var price by remember { mutableStateOf(dish?.price?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (dish == null) "Add Dish" else "Edit Dish") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AtomoTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                AtomoTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                AtomoTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceVal = price.toDoubleOrNull() ?: 0.0
                onSave(name, description, priceVal, null)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DigitalMenuListScreen(state: DigitalMenuState, onAction: (DigitalMenuAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(DigitalMenuAction.CreateMenu) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Menu")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.menus.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues)) {
                DigitalMenuShimmer()
            }
        } else {
            if (state.menus.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(DigitalMenuAction.UpgradePlan) }
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
                    items(state.menus) { menu ->
                        DigitalMenuItem(menu = menu, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalMenuItem(
    menu: Menu,
    onAction: (DigitalMenuAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(DigitalMenuAction.OpenMenu(menu.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = menu.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (menu.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
             // Add delete if needed, but not in original? Added for consistency
             IconButton(onClick = { onAction(DigitalMenuAction.DeleteMenu(menu.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Menu")
             }
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