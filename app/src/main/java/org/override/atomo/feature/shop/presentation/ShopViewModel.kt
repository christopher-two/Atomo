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
import java.util.UUID

class ShopViewModel(
    private val shopUseCases: ShopUseCases
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
        }
    }

    private fun loadShops() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userId = "test_user_id" // TODO
            shopUseCases.getShops(userId).collect { list ->
                _state.update { it.copy(isLoading = false, shops = list) }
            }
        }
    }

    private fun createShop() {
        viewModelScope.launch {
            val newShop = Shop(
                id = UUID.randomUUID().toString(),
                userId = "test_user_id",
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