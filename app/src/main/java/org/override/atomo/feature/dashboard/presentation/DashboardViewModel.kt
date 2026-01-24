package org.override.atomo.feature.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.session.api.SessionRepository

class DashboardViewModel(
    private val sessionRepository: SessionRepository,
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val portfolioUseCases: PortfolioUseCases,
    private val cvUseCases: CvUseCases,
    private val shopUseCases: ShopUseCases,
    private val invitationUseCases: InvitationUseCases,
    private val rootNavigation: RootNavigation
) : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _state = MutableStateFlow(DashboardState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DashboardState()
        )

    init {
        loadDashboardData()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> loadDashboardData()
            
            // Navigation to edit existing services
            is DashboardAction.NavigateToMenu -> rootNavigation.navTo(RouteApp.EditDigitalMenu(action.menuId))
            is DashboardAction.NavigateToPortfolio -> rootNavigation.navTo(RouteApp.EditPortfolio(action.portfolioId))
            is DashboardAction.NavigateToCv -> rootNavigation.navTo(RouteApp.EditCV(action.cvId))
            is DashboardAction.NavigateToShop -> rootNavigation.navTo(RouteApp.EditShop(action.shopId))
            is DashboardAction.NavigateToInvitation -> rootNavigation.navTo(RouteApp.EditInvitation(action.invitationId))
            
            // Quick create new services
            DashboardAction.CreateMenu -> rootNavigation.navTo(RouteApp.CreateDigitalMenu)
            DashboardAction.CreatePortfolio -> rootNavigation.navTo(RouteApp.CreatePortfolio)
            DashboardAction.CreateCv -> rootNavigation.navTo(RouteApp.CreateCV)
            DashboardAction.CreateShop -> rootNavigation.navTo(RouteApp.CreateShop)
            DashboardAction.CreateInvitation -> rootNavigation.navTo(RouteApp.CreateInvitation)
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val userId = sessionRepository.getCurrentUserId().firstOrNull()
            Log.d(TAG, "loadDashboardData: userId = $userId")
            
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }
            
            // Sync profile from remote first
            profileUseCases.syncProfile(userId)
                .onSuccess { profile ->
                    Log.d(TAG, "Profile synced: ${profile.displayName}")
                    _state.update { it.copy(profile = profile) }
                }
                .onFailure { error ->
                    Log.e(TAG, "Profile sync failed: ${error.message}")
                    // Try to get from local cache
                    profileUseCases.getProfile(userId).firstOrNull()?.let { profile ->
                        _state.update { it.copy(profile = profile) }
                    }
                }
            
            // Sync ALL services from remote and WAIT for completion using async/await
            val syncJobs = listOf(
                async { 
                    menuUseCases.syncMenus(userId)
                        .onSuccess { Log.d(TAG, "Menus synced: ${it.size}") }
                        .onFailure { Log.e(TAG, "Menus sync failed: ${it.message}") }
                },
                async { 
                    portfolioUseCases.syncPortfolios(userId)
                        .onSuccess { Log.d(TAG, "Portfolios synced: ${it.size}") }
                        .onFailure { Log.e(TAG, "Portfolios sync failed: ${it.message}") }
                },
                async { 
                    cvUseCases.syncCvs(userId)
                        .onSuccess { Log.d(TAG, "CVs synced: ${it.size}") }
                        .onFailure { Log.e(TAG, "CVs sync failed: ${it.message}") }
                },
                async { 
                    shopUseCases.syncShops(userId)
                        .onSuccess { Log.d(TAG, "Shops synced: ${it.size}") }
                        .onFailure { Log.e(TAG, "Shops sync failed: ${it.message}") }
                },
                async { 
                    invitationUseCases.syncInvitations(userId)
                        .onSuccess { Log.d(TAG, "Invitations synced: ${it.size}") }
                        .onFailure { Log.e(TAG, "Invitations sync failed: ${it.message}") }
                }
            )
            
            // Wait for all syncs to complete
            syncJobs.awaitAll()
            Log.d(TAG, "All syncs completed, now collecting flows")
            
            // Now combine all service flows - data should be in Room
            combine(
                menuUseCases.getMenus(userId),
                portfolioUseCases.getPortfolios(userId),
                cvUseCases.getCvs(userId),
                shopUseCases.getShops(userId),
                invitationUseCases.getInvitations(userId)
            ) { menus, portfolios, cvs, shops, invitations ->
                Log.d(TAG, "Flows updated: menus=${menus.size}, portfolios=${portfolios.size}, cvs=${cvs.size}, shops=${shops.size}, invitations=${invitations.size}")
                
                val services = mutableListOf<ServiceModule>()
                
                // Menu Module - always add
                val allDishes = menus.flatMap { it.dishes }
                Log.d(TAG, "Menu details: menus=${menus.map { "${it.name}(${it.dishes.size} dishes)" }}")
                services.add(
                    ServiceModule.MenuModule(
                        menus = menus,
                        totalDishes = allDishes.size,
                        recentDishes = allDishes.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                // Portfolio Module - always add
                val allItems = portfolios.flatMap { it.items }
                services.add(
                    ServiceModule.PortfolioModule(
                        portfolios = portfolios,
                        totalItems = allItems.size,
                        recentItems = allItems.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                // CV Module - always add
                val totalSkills = cvs.sumOf { it.skills.size }
                val totalExperiences = cvs.sumOf { it.experience.size }
                services.add(
                    ServiceModule.CvModule(
                        cvs = cvs,
                        totalSkills = totalSkills,
                        totalExperiences = totalExperiences
                    )
                )
                
                // Shop Module - always add
                val allProducts = shops.flatMap { it.products }
                services.add(
                    ServiceModule.ShopModule(
                        shops = shops,
                        totalProducts = allProducts.size,
                        recentProducts = allProducts.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                // Invitation Module - always add
                val activeInvitations = invitations.filter { it.isActive }
                val upcomingEvent = invitations
                    .filter { it.eventDate != null && it.eventDate > System.currentTimeMillis() }
                    .minByOrNull { it.eventDate ?: Long.MAX_VALUE }
                services.add(
                    ServiceModule.InvitationModule(
                        invitations = invitations,
                        activeCount = activeInvitations.size,
                        upcomingEvent = upcomingEvent
                    )
                )
                
                services.toList()
            }.collect { services ->
                Log.d(TAG, "Updating state with ${services.size} services")
                _state.update { 
                    it.copy(
                        isLoading = false,
                        services = services // Show ALL services
                    ) 
                }
            }
        }
    }
}