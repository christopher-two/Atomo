package org.override.atomo.feature.shop.presentation

sealed interface ShopAction {
    data object CreateShop : ShopAction
    data class DeleteShop(val id: String) : ShopAction
    data class OpenShop(val id: String) : ShopAction
    data object UpgradePlan : ShopAction
}