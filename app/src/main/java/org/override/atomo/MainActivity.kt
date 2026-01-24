package org.override.atomo

import android.os.Bundle
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
    private var isCheckingSession = false

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Mantener el splash visible mientras se verifica la sesión
        splashScreen.setKeepOnScreenCondition { isCheckingSession }

        // Verificar sesión y navegar según el resultado
        lifecycleScope.launch {
            val isLoggedIn = sessionRepository.isUserLoggedIn().first()
            rootNavigation.setInitialRoute(isLoggedIn)
            isCheckingSession = true
        }

        enableEdgeToEdge()
        setContent {
            AtomoTheme {
                WrapperRootNavigation()
            }
        }
    }
}
