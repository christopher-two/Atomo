/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.core.ui.local.LocalSharedTransitionScope
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.main.MainViewModel
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.navigation.wrapper.WrapperRootNavigation

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by inject()
    private val rootNavigation: RootNavigation by inject()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collect { state ->
                    splashScreen.setKeepOnScreenCondition {
                        !state.isSessionChecked || state.isLoading
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            val mainState by mainViewModel.state.collectAsStateWithLifecycle()
            val themeConfig = mainState.themeConfig

            val isDarkTheme = themeConfig.isDarkMode ?: isSystemInDarkTheme()

            AtomoTheme(
                darkTheme = isDarkTheme,
                useDynamicColor = themeConfig.useDynamicColors,
                seedColor = themeConfig.seedColor
            ) {
                SharedTransitionLayout {
                    CompositionLocalProvider(
                        value = LocalSharedTransitionScope provides this,
                        content = { WrapperRootNavigation() }
                    )
                }
            }
        }
    }
}
