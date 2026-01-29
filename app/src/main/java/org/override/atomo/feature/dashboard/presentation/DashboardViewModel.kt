/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

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
import kotlinx.coroutines.channels.Channel
import org.override.atomo.feature.navigation.HomeNavigation
import org.override.atomo.feature.navigation.AppTab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import kotlinx.coroutines.flow.receiveAsFlow

import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase

import org.override.atomo.domain.util.AtomoUrlGenerator

class DashboardViewModel(
    private val sessionRepository: SessionRepository,
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val portfolioUseCases: PortfolioUseCases,
    private val cvUseCases: CvUseCases,
    private val shopUseCases: ShopUseCases,
    private val invitationUseCases: InvitationUseCases,
    private val syncAllServices: SyncAllServicesUseCase,
    private val rootNavigation: RootNavigation,
    private val homeNavigation: HomeNavigation
) : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private var hasAutoRefreshed = false

    private val _state = MutableStateFlow(DashboardState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DashboardState()
        )
        
    private val _events = Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeLocalData()
    }

    /**
     * Handles user actions from the UI.
     * Use this method to process intents such as refreshing data, editing services, or deleting items.
     *
     * @param action The action to perform.
     */
    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> refreshDashboard()

            /* Edit actions - open bottom sheet */
            is DashboardAction.EditMenu -> _state.update { it.copy(activeSheet = DashboardSheet.EditMenu(action.menuId)) }
            is DashboardAction.EditPortfolio -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolio(action.portfolioId)) }
            is DashboardAction.EditCv -> _state.update { it.copy(activeSheet = DashboardSheet.EditCv(action.cvId)) }
            is DashboardAction.EditShop -> _state.update { it.copy(activeSheet = DashboardSheet.EditShop(action.shopId)) }
            is DashboardAction.EditInvitation -> _state.update { it.copy(activeSheet = DashboardSheet.EditInvitation(action.invitationId)) }

            /* Update actions - save from sheet */
            is DashboardAction.UpdateMenu -> updateService { menuUseCases.updateMenu(action.menu).map { } }
            is DashboardAction.UpdatePortfolio -> updateService { portfolioUseCases.updatePortfolio(action.portfolio).map { } }
            is DashboardAction.UpdateCv -> updateService { cvUseCases.updateCv(action.cv).map { } }
            is DashboardAction.UpdateShop -> updateService { shopUseCases.updateShop(action.shop).map { } }
            is DashboardAction.UpdateInvitation -> updateService { invitationUseCases.updateInvitation(action.invitation).map { } }

            /* Sub-item actions */
            is DashboardAction.AddDish -> _state.update { it.copy(activeSheet = DashboardSheet.EditDish(null, action.menuId)) }
            is DashboardAction.EditDish -> _state.update { it.copy(activeSheet = DashboardSheet.EditDish(action.dish, action.dish.menuId)) }
            is DashboardAction.UpdateDish -> {
                updateService {
                    if (action.dish.id.isEmpty()) {
                        // Generate ID for new dish
                        val newDish = action.dish.copy(id = java.util.UUID.randomUUID().toString())
                        menuUseCases.createDish(newDish).map { }
                    } else {
                        menuUseCases.updateDish(action.dish).map { }
                    }
                }
            }

            is DashboardAction.AddPortfolioItem -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolioItem(null, action.portfolioId)) }
            is DashboardAction.EditPortfolioItem -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolioItem(action.item, action.item.portfolioId)) }

            /* Sheet actions */
            DashboardAction.DismissSheet -> _state.update { it.copy(activeSheet = null) }

            /* Delete confirmation */
            is DashboardAction.ConfirmDeleteMenu -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteMenu(action.menu)) }
            is DashboardAction.ConfirmDeletePortfolio -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeletePortfolio(action.portfolio)) }
            is DashboardAction.ConfirmDeleteCv -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteCv(action.cv)) }
            is DashboardAction.ConfirmDeleteShop -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteShop(action.shop)) }
            is DashboardAction.ConfirmDeleteInvitation -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteInvitation(action.invitation)) }

            /* Share actions - TODO: implement share */
            is DashboardAction.ShareMenu -> { /* TODO: share menu link */ }
            is DashboardAction.SharePortfolio -> { /* TODO: share portfolio link */ }
            is DashboardAction.ShareCv -> { /* TODO: share CV link */ }
            is DashboardAction.ShareShop -> { /* TODO: share shop link */ }
            is DashboardAction.ShareInvitation -> { /* TODO: share invitation link */ }

            /* Dialog actions */
            DashboardAction.DismissDeleteDialog -> _state.update { it.copy(deleteDialog = null) }
            DashboardAction.ConfirmDelete -> deleteService()

            /* New Service Card Actions */
            is DashboardAction.PreviewService -> {
                val username = _state.value.profile?.username
                if (username != null) {
                    val url = AtomoUrlGenerator.generateServiceUrl(username, action.type)
                    viewModelScope.launch {
                        _events.send(DashboardEvent.OpenUrl(url))
                    }
                } else {
                    viewModelScope.launch {
                        _events.send(DashboardEvent.ShowSnackbar("No se pudo obtener el nombre de usuario"))
                    }
                }
            }
            is DashboardAction.ShowQR -> { /* TODO: Show QR Dialog */ }
            is DashboardAction.ShareService -> {
                val username = _state.value.profile?.username
                if (username != null) {
                    val url = AtomoUrlGenerator.generateServiceUrl(username, action.type)
                    viewModelScope.launch {
                        _events.send(DashboardEvent.ShareUrl(url, "Mira mi ${action.type.name.lowercase().replace("_", " ")} en Atomo"))
                    }
                } else {
                     viewModelScope.launch {
                        _events.send(DashboardEvent.ShowSnackbar("No se pudo obtener el nombre de usuario"))
                    }
                }
            }

            /* Create new services (Switch to tab) */
            DashboardAction.CreateMenu -> homeNavigation.switchTab(AppTab.DIGITAL_MENU)
            DashboardAction.CreatePortfolio -> homeNavigation.switchTab(AppTab.PORTFOLIO)
            DashboardAction.CreateCv -> homeNavigation.switchTab(AppTab.CV)
            DashboardAction.CreateShop -> homeNavigation.switchTab(AppTab.SHOP)
            DashboardAction.CreateInvitation -> homeNavigation.switchTab(AppTab.INVITATION)
        }
    }
    
    private fun updateService(updateCall: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _state.update { it.copy(isOperationLoading = true) }
            val result = updateCall()
            
            result.onSuccess {
                _state.update { it.copy(activeSheet = null, isOperationLoading = false) }
                _events.send(DashboardEvent.ShowSnackbar("Cambios guardados correctamente"))
            }.onFailure { error ->
                    _state.update { it.copy(isOperationLoading = false, error = "Error al actualizar: ${error.message}") }
                    _events.send(DashboardEvent.ShowSnackbar("Error: ${error.message}"))
                }
        }
    }
    


    private fun deleteService() {
        val currentState = _state.value
        val dialogState = currentState.deleteDialog ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isOperationLoading = true, deleteDialog = null) }
            
            val result = when (dialogState) {
                is DeleteDialogState.DeleteMenu -> menuUseCases.deleteMenu(dialogState.menu.id)
                is DeleteDialogState.DeletePortfolio -> portfolioUseCases.deletePortfolio(dialogState.portfolio.id)
                is DeleteDialogState.DeleteCv -> cvUseCases.deleteCv(dialogState.cv.id)
                is DeleteDialogState.DeleteShop -> shopUseCases.deleteShop(dialogState.shop.id)
                is DeleteDialogState.DeleteInvitation -> invitationUseCases.deleteInvitation(dialogState.invitation.id)
            }
            
            result
                .onSuccess {
                    Log.d(TAG, "Service deleted successfully")
                    _state.update { it.copy(isOperationLoading = false) }
                    _events.send(DashboardEvent.ShowSnackbar("Servicio eliminado correctamente"))
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to delete service", error)
                    _state.update { it.copy(isOperationLoading = false, error = "Error al eliminar: ${error.message}") }
                    _events.send(DashboardEvent.ShowSnackbar("Error al eliminar: ${error.message}"))
                }
        }
    }

    /**
     * Optimized: Loads only local data.
     * Observes the Room DB and updates the UI. Does not perform network requests.
     */
    private fun observeLocalData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val userId = sessionRepository.getCurrentUserId().firstOrNull()
            Log.d(TAG, "observeLocalData: userId = $userId")
            
            if (userId == null) {
                _state.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }
            
            /* Get local profile if available */
            profileUseCases.getProfile(userId).firstOrNull()?.let { profile ->
                _state.update { it.copy(profile = profile) }
            }
            
            /* Combine flows from Room DB */
            combine(
                menuUseCases.getMenus(userId),
                portfolioUseCases.getPortfolios(userId),
                cvUseCases.getCvs(userId),
                shopUseCases.getShops(userId),
                invitationUseCases.getInvitations(userId)
            ) { menus, portfolios, cvs, shops, invitations ->
                Log.d(TAG, "Local flows emitted: menus=${menus.size}")
                
                 val services = mutableListOf<ServiceModule>()
                
                /* Menu Module */
                val allDishes = menus.flatMap { it.dishes }
                services.add(
                    ServiceModule.MenuModule(
                        menus = menus,
                        totalDishes = allDishes.size,
                        recentDishes = allDishes.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                /* Portfolio Module */
                val allItems = portfolios.flatMap { it.items }
                services.add(
                    ServiceModule.PortfolioModule(
                        portfolios = portfolios,
                        totalItems = allItems.size,
                        recentItems = allItems.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                /* CV Module */
                val totalSkills = cvs.sumOf { it.skills.size }
                val totalExperiences = cvs.sumOf { it.experience.size }
                services.add(
                    ServiceModule.CvModule(
                        cvs = cvs,
                        totalSkills = totalSkills,
                        totalExperiences = totalExperiences
                    )
                )
                
                /* Shop Module */
                val allProducts = shops.flatMap { it.products }
                services.add(
                    ServiceModule.ShopModule(
                        shops = shops,
                        totalProducts = allProducts.size,
                        recentProducts = allProducts.sortedByDescending { it.createdAt }.take(5)
                    )
                )
                
                /* Invitation Module */
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
                val activeServicesCount = services.count { it.isActive }
                
                /* TODO: Replace with real analytics */
                val stats = DashboardStatistics(
                    activeServices = activeServicesCount,
                    totalViews = 0,
                    totalInteractions = 0
                )
                
                /* Stats are already calculated above */
                
                val shortcuts = DashboardHelpers.generateShortcuts(services)

                _state.update { 
                    it.copy(
                        isLoading = false,
                        services = services,
                        statistics = stats,
                        shortcuts = shortcuts
                    ) 
                }

                // Auto-refresh if no data is found locally and we haven't refreshed yet
                val hasNoServices = services.none { it.isActive }
                val hasNoProfile = _state.value.profile == null
                
                if ((hasNoServices || hasNoProfile) && !hasAutoRefreshed) {
                    Log.d(TAG, "No local data found. Triggering auto-refresh.")
                    hasAutoRefreshed = true
                    refreshDashboard()
                }
            }
        }
    }

    /**
     * Optimized: Synchronizes with the backend only when requested (Swipe Refresh).
     */
    private fun refreshDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }
            
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            
            syncAllServices(userId)
                .onFailure { Log.e(TAG, "Sync failed: ${it.message}") }
            
            Log.d(TAG, "Refresh completed")
            _state.update { it.copy(isRefreshing = false) }
        }
    }
}