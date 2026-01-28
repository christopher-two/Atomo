package org.override.atomo.feature.shop.presentation

import org.override.atomo.domain.model.Shop

data class ShopState(
    val isLoading: Boolean = false,
    val shops: List<Shop> = emptyList(),
    val error: String? = null,
    val canCreate: Boolean = false,
    val limitReached: Boolean = false,
    
    // Editor State
    val isEditing: Boolean = false,
    val editingShop: Shop? = null,
    val showPreviewSheet: Boolean = false
)