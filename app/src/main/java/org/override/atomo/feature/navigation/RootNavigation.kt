/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.override.atomo.core.common.RouteApp

class RootNavigation {
    // Iniciar con lista vacía para evitar mostrar una ruta por defecto antes de verificar la sesión
    private val _backstack = MutableStateFlow<MutableList<RouteApp>>(mutableListOf())
    val backstack = _backstack.asStateFlow()

    fun setInitialRoute(isLoggedIn: Boolean) {
        val initialRoute = if (isLoggedIn) RouteApp.Home else RouteApp.Auth
        _backstack.update { mutableListOf(initialRoute) }
    }

    fun back() {
        _backstack.update { current ->
            // Nunca dejemos el backstack vacío; siempre mantenemos al menos una ruta
            if (current.size <= 1) return@update current
            // Creamos una nueva lista para evitar mutar la lista compartida
            current.toMutableList().apply { removeLastOrNull() }
        }
    }

    /**
     * Reemplaza completamente el backstack con la nueva ruta.
     * Útil para navegación post-login/logout donde no queremos que el usuario pueda volver atrás.
     */
    fun replaceWith(route: RouteApp) {
        _backstack.update { mutableListOf(route) }
    }

    fun navQr(data: String?) {
        _backstack.update { current ->
            current.toMutableList().apply { add(RouteApp.Qr(data)) }
        }
    }

    fun navTo(
        route: RouteApp
    ) {
        // Si el backstack está vacío no hacemos nada
        val current = _backstack.value
        if (current.isEmpty()) return

        // Si ya es la ruta actual, ignoramos
        if (current.last() == route) return

        // Si la ruta existe en el backstack hacemos un "pop-to" hasta esa ruta
        if (current.contains(route)) {
            _backstack.update { navKeys ->
                val idx = navKeys.indexOf(route)
                if (idx >= 0) {
                    // crear una nueva lista que contenga los elementos hasta e incluyendo 'route'
                    navKeys.subList(0, idx + 1).toMutableList()
                } else {
                    navKeys
                }
            }
            return
        }

        // Si no está en el backstack, añadimos una nueva entrada (nueva lista para evitar aliasing)
        _backstack.update { navKeys -> navKeys.toMutableList().apply { add(route) } }
    }
}