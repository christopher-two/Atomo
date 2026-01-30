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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase
import org.override.atomo.domain.util.AtomoUrlGenerator
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.HomeNavigation
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
    private val syncAllServices: SyncAllServicesUseCase,
    private val rootNavigation: RootNavigation,
    private val homeNavigation: HomeNavigation,
    private val syncManager: org.override.atomo.data.manager.SyncManager
) : ViewModel() {


    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private var hasAutoRefreshed = false

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    private val _events = kotlinx.coroutines.channels.Channel<DashboardEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeLocalData()
        triggerSync()
    }

    private fun triggerSync() {
        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            syncManager.scheduleDataSync(userId)
        }
    }


    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.Refresh -> refreshDashboard()

            /* Edit actions */
            is DashboardAction.EditMenu -> _state.update { it.copy(activeSheet = DashboardSheet.EditMenu(action.menuId)) }
            is DashboardAction.EditPortfolio -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolio(action.portfolioId)) }
            is DashboardAction.EditCv -> _state.update { it.copy(activeSheet = DashboardSheet.EditCv(action.cvId)) }
            is DashboardAction.EditShop -> _state.update { it.copy(activeSheet = DashboardSheet.EditShop(action.shopId)) }
            is DashboardAction.EditInvitation -> _state.update { it.copy(activeSheet = DashboardSheet.EditInvitation(action.invitationId)) }

            /* Update actions */
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
                        val newDish = action.dish.copy(id = java.util.UUID.randomUUID().toString())
                        menuUseCases.createDish(newDish).map { }
                    } else {
                        menuUseCases.updateDish(action.dish).map { }
                    }
                }
            }

            is DashboardAction.AddPortfolioItem -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolioItem(null, action.portfolioId)) }
            is DashboardAction.EditPortfolioItem -> _state.update { it.copy(activeSheet = DashboardSheet.EditPortfolioItem(action.item, action.item.portfolioId)) }

            DashboardAction.DismissSheet -> _state.update { it.copy(activeSheet = null) }

            /* Delete confirmation */
            is DashboardAction.ConfirmDeleteMenu -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteMenu(action.menu)) }
            is DashboardAction.ConfirmDeletePortfolio -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeletePortfolio(action.portfolio)) }
            is DashboardAction.ConfirmDeleteCv -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteCv(action.cv)) }
            is DashboardAction.ConfirmDeleteShop -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteShop(action.shop)) }
            is DashboardAction.ConfirmDeleteInvitation -> _state.update { it.copy(deleteDialog = DeleteDialogState.DeleteInvitation(action.invitation)) }

            DashboardAction.DismissDeleteDialog -> _state.update { it.copy(deleteDialog = null) }
            DashboardAction.ConfirmDelete -> deleteService()

            /* Service Card Actions */
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
            is DashboardAction.ShowQR -> {
                val username = _state.value.profile?.username
                if (username != null) {
                    val url = AtomoUrlGenerator.generateServiceUrl(username, action.type)
                    rootNavigation.navQr(
                        data = url
                    )
                }
            }
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
            else -> {}
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

    private fun observeLocalData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            
            profileUseCases.getProfile(userId).collect { profile ->
                _state.update { it.copy(profile = profile) }
            }
        }

        viewModelScope.launch {
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            combine(
                menuUseCases.getMenus(userId),
                portfolioUseCases.getPortfolios(userId),
                cvUseCases.getCvs(userId),
                shopUseCases.getShops(userId),
                invitationUseCases.getInvitations(userId)
            ) { menus, portfolios, cvs, shops, invitations ->
                val services = mutableListOf<ServiceModule>()
                
                val allDishes = menus.flatMap { it.dishes }
                services.add(ServiceModule.MenuModule(menus, allDishes.size, allDishes.sortedByDescending { it.createdAt }.take(5)))
                
                val allItems = portfolios.flatMap { it.items }
                services.add(ServiceModule.PortfolioModule(portfolios, allItems.size, allItems.sortedByDescending { it.createdAt }.take(5)))
                
                services.add(ServiceModule.CvModule(cvs, cvs.sumOf { it.skills.size }, cvs.sumOf { it.experience.size }))
                
                val allProducts = shops.flatMap { it.products }
                services.add(ServiceModule.ShopModule(shops, allProducts.size, allProducts.sortedByDescending { it.createdAt }.take(5)))
                
                services.add(ServiceModule.InvitationModule(invitations, invitations.count { it.isActive }, invitations.filter { (it.eventDate ?: 0) > System.currentTimeMillis() }.minByOrNull { it.eventDate ?: Long.MAX_VALUE }))

                services
            }.collect { services ->
                val stats = DashboardStatistics(services.count { it.isActive }, 0, 0)
                val shortcuts = DashboardHelpers.generateShortcuts(services)
                _state.update { it.copy(isLoading = false, services = services, statistics = stats, shortcuts = shortcuts) }
                
                if (services.none { it.isActive } && !hasAutoRefreshed) {
                    hasAutoRefreshed = true
                    refreshDashboard()
                }
            }
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            val userId = sessionRepository.getCurrentUserId().firstOrNull() ?: return@launch
            syncManager.scheduleDataSync(userId)

            // For UX, wait a bit or just let it finish. 
            // In a real app we would observe WorkInfo.
            // For now, simple delay to show spinner or remove spinner immediately and let loading flow handle it.
            kotlinx.coroutines.delay(1000) 
            _state.update { it.copy(isRefreshing = false) }
        }
    }

}
