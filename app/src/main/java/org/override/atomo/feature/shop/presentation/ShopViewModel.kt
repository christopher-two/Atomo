package org.override.atomo.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.domain.model.Shop
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.subscription.CanCreateResult
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.feature.home.presentation.ServiceType
import java.util.UUID


class ShopViewModel(
    private val shopUseCases: ShopUseCases,
    private val canCreateServiceUseCase: CanCreateServiceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShopState())
    val state = _state
        .onStart { loadShops() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ShopState(),
        )

    fun onAction(action: ShopAction) {
        when (action) {
            is ShopAction.CreateShop -> createShop()
            is ShopAction.DeleteShop -> deleteShop(action.id)
            is ShopAction.OpenShop -> { /* Handle navigation */ }
            is ShopAction.UpgradePlan -> { /* Handle navigation to pay/subscription */ }
        }
    }

    private fun loadShops() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO
            
            launch {
                shopUseCases.getShops(userId).collect { list ->
                    _state.update { it.copy(shops = list) }
                    checkCreationLimit(userId)
                }
            }
        }
    }
    
    private suspend fun checkCreationLimit(userId: String) {
        val result = canCreateServiceUseCase(userId, ServiceType.SHOP)
        _state.update { 
            it.copy(
                isLoading = false,
                canCreate = result is CanCreateResult.Success,
                limitReached = result is CanCreateResult.TotalLimitReached || result is CanCreateResult.ServiceTypeExists
            )
        }
    }

    private fun createShop() {
        viewModelScope.launch {
            val userId = "test_user_id" // TODO
            
            val result = canCreateServiceUseCase(userId, ServiceType.SHOP)
            if (result !is CanCreateResult.Success) {
                return@launch
            }
            
            val newShop = Shop(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "My Shop",
                description = "My awesome shop",
                isActive = true,
                primaryColor = "#000000",
                fontFamily = "Inter",
                createdAt = System.currentTimeMillis()
            )
            shopUseCases.createShop(newShop)
        }
    }

    private fun deleteShop(id: String) {
        viewModelScope.launch {
            shopUseCases.deleteShop(id)
        }
    }
}