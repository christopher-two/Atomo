package org.override.atomo.core.common

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RouteApp : NavKey {
    object Auth : RouteApp
    object Home : RouteApp
    object Settings : RouteApp
    
    // Service Creation Routes
    object CreateDigitalMenu : RouteApp
    object CreatePortfolio : RouteApp
    object CreateCV : RouteApp
    object CreateShop : RouteApp
    object CreateInvitation : RouteApp
    
    // Service Edit Routes
    @Serializable
    data class EditDigitalMenu(val menuId: String) : RouteApp
    @Serializable
    data class EditPortfolio(val portfolioId: String) : RouteApp
    @Serializable
    data class EditCV(val cvId: String) : RouteApp
    @Serializable
    data class EditShop(val shopId: String) : RouteApp
    @Serializable
    data class EditInvitation(val invitationId: String) : RouteApp
}