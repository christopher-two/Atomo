/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteMain : NavKey {
    object Dashboard : RouteMain
    object Profile : RouteMain
    object Pay : RouteMain
    object DigitalMenu : RouteMain
    object Shop : RouteMain
    object Cv : RouteMain
    object Portfolio : RouteMain
    object Invitation : RouteMain
}