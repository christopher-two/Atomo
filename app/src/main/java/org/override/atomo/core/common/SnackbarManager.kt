package org.override.atomo.core.common

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Global SnackbarManager for showing snackbar messages from anywhere in the app.
 * Injected via Koin as a singleton.
 */
class SnackbarManager {
    val snackbarHostState = SnackbarHostState()
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}
