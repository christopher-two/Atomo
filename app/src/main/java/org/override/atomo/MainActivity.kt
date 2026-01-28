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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.core.ui.local.LocalSharedTransitionScope
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.navigation.wrapper.WrapperRootNavigation
import org.override.atomo.feature.settings.domain.usecase.GetSettingsUseCase
import org.override.atomo.libs.session.api.SessionRepository

class MainActivity : ComponentActivity() {
    private val sessionRepository: SessionRepository by inject()
    private val getSettingsUseCase: GetSettingsUseCase by inject()
    private val rootNavigation: RootNavigation by inject()
    private var isCheckingSession = true
    private var isSettingsLoaded = false

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { isCheckingSession || !isSettingsLoaded }

        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Inicio comprobación de sesión")
                val isLoggedIn = sessionRepository.isUserLoggedIn().first()
                Log.d("MainActivity", "Resultado comprobación sesión: $isLoggedIn")
                rootNavigation.setInitialRoute(isLoggedIn)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error comprobando sesión inicial", e)
                rootNavigation.setInitialRoute(false)
            } finally {
                isCheckingSession = false
                Log.d("MainActivity", "Finalizada comprobación de sesión")
            }
        }

        enableEdgeToEdge()
        setContent {
            val settingsState by getSettingsUseCase().collectAsState(initial = null)

            if (settingsState != null && !isSettingsLoaded) {
                isSettingsLoaded = true
            }

            val appearance = settingsState?.appearance

            val isDarkTheme = appearance?.let {
                if (it.isSystemThemeEnabled) isSystemInDarkTheme() else it.isDarkModeEnabled
            } ?: isSystemInDarkTheme()

            val seedColor = when (appearance?.theme) {
                "pink" -> Color(0xFFFFB5E8)
                "green" -> Color(0xFFB5FFD9)
                "purple" -> Color(0xFFDDB5FF)
                "blue" -> Color(0xFFB5DEFF)
                else -> Color(0xFFDAEDFF) // Default/Auto
            }

            AtomoTheme(
                darkTheme = isDarkTheme,
                useDynamicColor = appearance?.isDynamicColorEnabled ?: false,
                seedColor = seedColor
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
