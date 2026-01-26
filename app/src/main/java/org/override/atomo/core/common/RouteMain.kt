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