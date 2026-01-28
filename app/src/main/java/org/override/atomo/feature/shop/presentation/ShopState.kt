package org.override.atomo.feature.shop.presentation

data class ShopState(
    val isLoading: Boolean = false,
    val shops: List<org.override.atomo.domain.model.Shop> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingShop: org.override.atomo.domain.model.Shop? = null,
    val showPreviewSheet: Boolean = false
)