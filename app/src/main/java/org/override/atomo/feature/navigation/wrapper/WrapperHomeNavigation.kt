package org.override.atomo.feature.navigation.wrapper

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.feature.navigation.HomeNavigation

@OptIn(KoinExperimentalAPI::class)
@Composable
fun WrapperHomeNavigation(
    homeNavigation: HomeNavigation = koinInject<HomeNavigation>(),
) {
    val rootBackStack = homeNavigation.currentStack

    NavDisplay(
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { homeNavigation.back() },
        entryProvider = koinEntryProvider(),

        // Navegación hacia adelante (Push)
        transitionSpec = {
            // Entra: Sube desde abajo con rebote
            (slideInVertically(
                initialOffsetY = { it }, // Empieza debajo de la pantalla
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            ) + fadeIn()) togetherWith
                    // Sale: Se encoge hacia el fondo (efecto "hacia adentro")
                    (scaleOut(
                        targetScale = 0.92f, // Se reduce un poco para dar profundidad
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut())
        },

        // Navegación hacia atrás (Pop)
        popTransitionSpec = {
            // Entra: Viene del fondo recuperando su tamaño
            (scaleIn(
                initialScale = 0.92f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
            ) + fadeIn()) togetherWith
                    // Sale: Baja hacia afuera
                    (slideOutVertically(
                        targetOffsetY = { it }, // Termina debajo de la pantalla
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                    ) + fadeOut())
        },

        // Gesto predictivo (Back Gesture)
        predictivePopTransitionSpec = {
            (scaleIn(initialScale = 0.92f) + fadeIn()) togetherWith
                    (slideOutVertically(targetOffsetY = { it }) + fadeOut())
        },
    )
}