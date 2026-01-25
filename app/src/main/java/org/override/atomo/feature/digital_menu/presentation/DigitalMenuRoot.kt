package org.override.atomo.feature.digital_menu.presentation

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.components.AtomoTextField
import org.override.atomo.core.ui.theme.AtomoTheme

@Composable
fun DigitalMenuRoot(
    viewModel: DigitalMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DigitalMenuScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DigitalMenuScreen(
    state: DigitalMenuState,
    onAction: (DigitalMenuAction) -> Unit,
) {
    // Mantener referencia al WebView y flag de carga en estado Compose (editor)
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    val pageLoaded = remember { mutableStateOf(false) }
    val pendingPreview = remember { mutableStateOf<String?>(null) }

    // Preview bottom sheet states
    val showPreviewSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val previewWebViewState = remember { mutableStateOf<WebView?>(null) }
    val previewPageLoaded = remember { mutableStateOf(false) }
    val previewPendingPreview = remember { mutableStateOf<String?>(null) }

    // Función auxiliar para construir JSON seguro
    fun buildPreviewJson(name: String, description: String): String {
        return JSONObject().apply {
            put("name", name)
            put("description", description)
        }.toString()
    }

    // Intenta enviar el preview al WebView del editor; si no está cargada, lo deja pendiente
    fun trySendPreviewToEditor(json: String) {
        val wv = webViewState.value
        if (wv != null && pageLoaded.value) {
            wv.post {
                wv.evaluateJavascript("updatePreview($json)", null)
            }
            pendingPreview.value = null
        } else {
            pendingPreview.value = json
        }
    }

    // Intenta enviar el preview al WebView del preview sheet; si no está cargada, lo deja pendiente
    fun trySendPreviewToSheet(json: String) {
        val wv = previewWebViewState.value
        if (wv != null && previewPageLoaded.value) {
            wv.post {
                wv.evaluateJavascript("updatePreview($json)", null)
            }
            previewPendingPreview.value = null
        } else {
            previewPendingPreview.value = json
        }
    }

    // Envía a ambos (editor + sheet)
    fun trySendPreviewToAll(json: String) {
        trySendPreviewToEditor(json)
        trySendPreviewToSheet(json)
    }

    AtomoScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Menu") },
                navigationIcon = {
                    IconButton(onClick = { onAction(DigitalMenuAction.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier.padding(16.dp),
            ) {
                IconButton(
                    onClick = { onAction(DigitalMenuAction.Back) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back"
                    )
                }
                IconButton(
                    onClick = { onAction(DigitalMenuAction.SaveMenu) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Back"
                    )
                }
                IconButton(onClick = { showPreviewSheet.value = true }) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Preview")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Info Section
            Text(
                text = "General Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            AtomoTextField(
                value = state.menuName,
                onValueChange = { newValue ->
                    onAction(DigitalMenuAction.UpdateName(newValue))
                },
                label = { Text("Menu Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AtomoTextField(
                value = state.menuDescription,
                onValueChange = { newValue ->
                    onAction(DigitalMenuAction.UpdateDescription(newValue))
                },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }

    // Bottom sheet full-screen preview
    if (showPreviewSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showPreviewSheet.value = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                    // Si hay preview pendiente, enviarlo ahora
                                    previewPendingPreview.value?.let { trySendPreviewToSheet(it) }
                                }
                            }
                            loadUrl("https://atomo.click/preview/default")
                            previewWebViewState.value = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
        }
    }

    // Observa los cambios en el estado y envía actualizaciones con debounce
    LaunchedEffect(state.menuName, state.menuDescription) {
        // Debounce corto para agrupar cambios rápidos
        delay(300)
        val json = buildPreviewJson(state.menuName, state.menuDescription)
        trySendPreviewToAll(json)
    }
}

@Composable
fun MenuEditorScreen(
    onWebViewCreated: (WebView) -> Unit = {},
    onPageFinished: () -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                // Cargar la preview
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onPageFinished()
                    }
                }
                loadUrl("https://atomo.click/preview/default")
                onWebViewCreated(this)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        DigitalMenuScreen(
            state = DigitalMenuState(),
            onAction = {}
        )
    }
}