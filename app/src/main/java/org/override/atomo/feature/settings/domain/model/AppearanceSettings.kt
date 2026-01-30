/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.domain.model

data class AppearanceSettings(
    val isDarkModeEnabled: Boolean,
    val theme: String, // "auto", "pink", "blue", "green", "purple"
    val isDynamicColorEnabled: Boolean,
    val isSystemThemeEnabled: Boolean
)
