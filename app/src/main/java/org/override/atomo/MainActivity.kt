package org.override.atomo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.feature.navigation.wrapper.WrapperRootNavigation
import org.override.atomo.libs.session.api.SessionRepository

class MainActivity : ComponentActivity() {
    private val sessionRepository: SessionRepository by inject()
    private val rootNavigation: RootNavigation by inject()
    // Mantener true mientras hacemos la comprobación inicial; la splash se mantiene mientras esto sea true
    private var isCheckingSession = true

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Mantener el splash visible mientras se verifica la sesión
        splashScreen.setKeepOnScreenCondition { isCheckingSession }

        // Verificar sesión y navegar según el resultado
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Inicio comprobación de sesión")
                val isLoggedIn = sessionRepository.isUserLoggedIn().first()
                Log.d("MainActivity", "Resultado comprobación sesión: $isLoggedIn")
                rootNavigation.setInitialRoute(isLoggedIn)
            } catch (e: Exception) {
                // Registrar el error y navegar a auth como fallback
                Log.e("MainActivity", "Error comprobando sesión inicial", e)
                rootNavigation.setInitialRoute(false)
            } finally {
                // Siempre liberar la splash para evitar bloqueo indefinido
                isCheckingSession = false
                Log.d("MainActivity", "Finalizada comprobación de sesión, splash liberada")
            }
        }

        enableEdgeToEdge()
        setContent {
            AtomoTheme {
                WrapperRootNavigation()
            }
        }
    }
}
