/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.navigation

import org.junit.Assert.*
import org.junit.Test
import org.override.atomo.core.common.RouteApp

class RootNavigationTest {

    @Test
    fun `navTo pushes new route when not present`() {
        val nav = RootNavigation()
        nav.replaceWith(RouteApp.Home)
        nav.navTo(RouteApp.Settings)

        val backstack = nav.backstack.value
        assertEquals(2, backstack.size)
        assertEquals(RouteApp.Home, backstack[0])
        assertEquals(RouteApp.Settings, backstack[1])
    }

    @Test
    fun `navTo pops to existing route`() {
        val nav = RootNavigation()
        nav.replaceWith(RouteApp.Home)
        nav.navTo(RouteApp.Settings)
        nav.navTo(RouteApp.CreateDigitalMenu)
        // ahora pop-to Settings
        nav.navTo(RouteApp.Settings)

        val backstack = nav.backstack.value
        assertEquals(2, backstack.size)
        assertEquals(RouteApp.Home, backstack[0])
        assertEquals(RouteApp.Settings, backstack[1])
    }

    @Test
    fun `back does not clear last route`() {
        val nav = RootNavigation()
        nav.replaceWith(RouteApp.Home)
        nav.back()
        val backstack = nav.backstack.value
        assertEquals(1, backstack.size)
        assertEquals(RouteApp.Home, backstack[0])
    }
}
