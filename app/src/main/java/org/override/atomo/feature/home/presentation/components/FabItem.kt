package org.override.atomo.feature.home.presentation.components

import androidx.compose.ui.graphics.vector.ImageVector
import org.override.atomo.feature.home.presentation.ServiceType

data class FabItem(
    val type: ServiceType,
    val icon: ImageVector,
    val label: String
)