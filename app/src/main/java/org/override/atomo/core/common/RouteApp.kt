package org.override.atomo.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteApp : NavKey {
    object Auth : RouteApp
    object Home : RouteApp
    object Settings : RouteApp
}