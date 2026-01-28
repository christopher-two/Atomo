package org.override.atomo.feature.cv.presentation

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
import org.override.atomo.domain.model.Cv
// CvItem import removed

@Composable
fun CVRoot(
    viewModel: CVViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    CVContent(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVContent(
    state: CVState,
    onAction: (CVAction) -> Unit
) {
    BackHandler(enabled = state.editingCv != null) {
        onAction(CVAction.Back)
    }

    // Preview Logic
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun buildPreviewJson(cv: Cv): String {
        return JSONObject().apply {
            put("title", cv.title)
            put("summary", cv.professionalSummary)
            put("templateId", cv.templateId)
            put("primaryColor", cv.primaryColor)
            put("fontFamily", cv.fontFamily)
        }.toString()
    }

    fun updatePreview(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post { wv.evaluateJavascript("updatePreview($json)", null) }
        }
    }

    LaunchedEffect(state.editingCv) {
        state.editingCv?.let {
            delay(300)
            updatePreview(buildPreviewJson(it))
        }
    }

    if (state.editingCv == null) {
        CVListScreen(state, onAction)
    } else {
        val cv = state.editingCv!!
        
        AtomoScaffold(
            topBar = {
                 TopAppBar(title = { Text(if (state.isEditing) "Edit CV" else cv.title) })
            },
            floatingActionButton = {
                 ServiceToolbar(
                     isEditing = state.isEditing,
                     onBack = { onAction(CVAction.Back) },
                     onEditVerify = { 
                         if (state.isEditing) onAction(CVAction.SaveCv) 
                         else onAction(CVAction.ToggleEditMode) 
                     },
                     onPreview = { onAction(CVAction.TogglePreviewSheet(true)) },
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
                 // General Info - Only visible in Edit mode since Title is in TopBar
                if (state.isEditing) {
                    EditableSection(title = "General Information", isEditing = true) {
                        AtomoTextField(
                            value = cv.title,
                            onValueChange = { onAction(CVAction.UpdateEditingCv(cv.copy(title = it))) },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Professional Summary
                EditableSection(title = "Professional Summary", isEditing = state.isEditing) {
                    if (state.isEditing) {
                        AtomoTextField(
                            value = cv.professionalSummary ?: "",
                            onValueChange = { onAction(CVAction.UpdateEditingCv(cv.copy(professionalSummary = it))) },
                            label = { Text("Summary") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    } else {
                        Text(cv.professionalSummary ?: "No summary provided", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                

                
                // Sections Placeholder
                EditableSection(title = "Experience & Education", isEditing = state.isEditing) {
                    Text("Manage your experience and education (Coming Soon)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }

                 Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f))
            }
        }
    }
    
    if (state.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { onAction(CVAction.TogglePreviewSheet(false)) },
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
                                state.editingCv?.let { updatePreview(buildPreviewJson(it)) }
                            }
                        }
                        loadUrl("https://atomo.click/preview/cv")
                        previewWebViewState.value = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CVListScreen(state: CVState, onAction: (CVAction) -> Unit) {
    AtomoScaffold(
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(CVAction.CreateCv) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create CV")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading && state.cvs.isEmpty()) {
            // Loading
            Box(Modifier.padding(paddingValues)) { Text("Loading...") }
        } else {
            if (state.cvs.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(CVAction.UpgradePlan) }
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
                    items(state.cvs) { cv ->
                         // Use CvItem if available or custom card
                        AtomoCard(
                            onClick = { onAction(CVAction.OpenCv(cv.id)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(cv.title, style = MaterialTheme.typography.titleLarge)
                                IconButton(onClick = { onAction(CVAction.DeleteCv(cv.id)) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}