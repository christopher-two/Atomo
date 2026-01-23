package org.override.atomo.core.common

import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteMain {
    object Menu : RouteMain
    object Profile : RouteMain
}