package org.override.atomo.feature.shop.presentation

sealed interface ShopAction {
    data object CreateShop : ShopAction
    data class DeleteShop(val id: String) : ShopAction
    data class OpenShop(val id: String) : ShopAction
    data object UpgradePlan : ShopAction
    
    // Editor Actions
    data object ToggleEditMode : ShopAction
    data class UpdateEditingShop(val shop: org.override.atomo.domain.model.Shop) : ShopAction
    data object SaveShop : ShopAction
    data object CancelEdit : ShopAction
    data class TogglePreviewSheet(val show: Boolean) : ShopAction
    data object Back : ShopAction
}