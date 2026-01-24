package org.override.atomo.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val label: String,
    val icon: ImageVector
) {
    MENU("Menu", Icons.Filled.Menu),
    PROFILE("Profile", Icons.Filled.Person)
}