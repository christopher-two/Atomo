/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard),
    PROFILE("Profile", Icons.Filled.Person),
    PAY("Pay", Icons.Filled.Payment),
    DIGITAL_MENU("Menu Digital", Icons.Filled.RestaurantMenu),
    SHOP("Tienda", Icons.Filled.ShoppingBag),
    CV("CV", Icons.Filled.Description),
    PORTFOLIO("Portafolio", Icons.Filled.Folder),
    INVITATION("Invitaciones", Icons.Filled.Email),

    // Special tab for mobile navigation sheet
    MENU("Menú", Icons.Filled.Menu) 
}