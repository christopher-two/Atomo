/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.main

import androidx.compose.ui.graphics.Color
import org.override.atomo.core.common.RouteApp

data class MainState(
    val isSessionChecked: Boolean = false,
    val isLoading: Boolean = true,
    val startDestination: RouteApp = RouteApp.Auth,
    val themeConfig: ThemeConfig = ThemeConfig()
)

data class ThemeConfig(
    val isDarkMode: Boolean? = null, // null = system, true = dark, false = light
    val seedColor: Color = Color(0xFFDAEDFF),
    val useDynamicColors: Boolean = false
)
