package org.override.atomo.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteMain : NavKey {
    object Menu : RouteMain
    object Profile : RouteMain
}