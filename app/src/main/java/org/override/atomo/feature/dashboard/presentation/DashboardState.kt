package org.override.atomo.feature.dashboard.presentation

import org.override.atomo.domain.model.Cv
import org.override.atomo.domain.model.Dish
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.Menu
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem
import org.override.atomo.domain.model.Product
import org.override.atomo.domain.model.Profile
import org.override.atomo.domain.model.Shop

data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val profile: Profile? = null,
    val services: List<ServiceModule> = emptyList()
)

sealed interface ServiceModule {
    val isActive: Boolean
    
    data class MenuModule(
        val menus: List<Menu>,
        val totalDishes: Int,
        val recentDishes: List<Dish>,
        override val isActive: Boolean = menus.isNotEmpty()
    ) : ServiceModule
    
    data class PortfolioModule(
        val portfolios: List<Portfolio>,
        val totalItems: Int,
        val recentItems: List<PortfolioItem>,
        override val isActive: Boolean = portfolios.isNotEmpty()
    ) : ServiceModule
    
    data class CvModule(
        val cvs: List<Cv>,
        val totalSkills: Int,
        val totalExperiences: Int,
        override val isActive: Boolean = cvs.isNotEmpty()
    ) : ServiceModule
    
    data class ShopModule(
        val shops: List<Shop>,
        val totalProducts: Int,
        val recentProducts: List<Product>,
        override val isActive: Boolean = shops.isNotEmpty()
    ) : ServiceModule
    
    data class InvitationModule(
        val invitations: List<Invitation>,
        val activeCount: Int,
        val upcomingEvent: Invitation? = null,
        override val isActive: Boolean = invitations.isNotEmpty()
    ) : ServiceModule
}