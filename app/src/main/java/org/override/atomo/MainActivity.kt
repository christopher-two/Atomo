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
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.core.ui.local.LocalSharedTransitionScope
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.main.MainViewModel
import org.override.atomo.feature.navigation.wrapper.WrapperRootNavigation

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by inject()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            val state = mainViewModel.state.value
            !state.isSessionChecked || state.isLoading
        }

        setContent {
            val mainState by mainViewModel.state.collectAsStateWithLifecycle()
            val themeConfig = mainState.themeConfig

            val isDarkTheme = themeConfig.isDarkMode ?: isSystemInDarkTheme()

            // Update System Bars based on theme
            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                    ) { isDarkTheme }
                )
            }

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
