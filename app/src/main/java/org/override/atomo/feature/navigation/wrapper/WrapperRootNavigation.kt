package org.override.atomo.feature.navigation.wrapper

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    // No renderizar nada hasta que el backstack tenga una ruta inicial
    // Esto evita que se muestre Auth por defecto antes de verificar la sesión
    if (rootBackStack.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Pantalla vacía mientras se determina la ruta inicial
            // La splash screen se mantiene visible hasta que isCheckingSession sea false
        }
        return
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