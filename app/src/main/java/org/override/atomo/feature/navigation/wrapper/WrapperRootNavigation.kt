/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.navigation.wrapper

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.feature.navigation.RootNavigation

@KoinExperimentalAPI
@Composable
fun WrapperRootNavigation() {
    val rootNavigation = koinInject<RootNavigation>()
    val rootBackStack by rootNavigation.backstack.collectAsStateWithLifecycle()
    if (rootBackStack.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
        }
        return
    }

    // Estado local para controlar si mostramos el diálogo de salida
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(
        onBack = {
            if (rootBackStack.size > 1) {
                rootNavigation.back()
            } else {
                // Si sólo queda una (o ninguna) ruta, preguntamos al usuario si desea salir
                showExitDialog = true
            }
        }
    )

    // Diálogo de confirmación para salir de la app
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Salir") },
            text = { Text(text = "¿Deseas salir de la aplicación?") },
            confirmButton = {
                TextButton(onClick = {
                    // Cerramos la Activity actual
                    (context as? Activity)?.finish()
                }) {
                    Text(text = "Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    NavDisplay(
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { rootNavigation.back() },
        entryProvider = koinEntryProvider(),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
    )
}