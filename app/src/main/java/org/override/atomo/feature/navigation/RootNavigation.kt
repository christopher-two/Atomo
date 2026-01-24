package org.override.atomo.feature.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.override.atomo.core.common.RouteApp

class RootNavigation {
    private val _backstack = MutableStateFlow<MutableList<RouteApp>>(mutableListOf(RouteApp.Auth))
    val backstack = _backstack.asStateFlow()

    fun setInitialRoute(isLoggedIn: Boolean) {
        val initialRoute = if (isLoggedIn) RouteApp.Home else RouteApp.Auth
        _backstack.update { mutableListOf(initialRoute) }
    }

    fun back() {
        _backstack.update {
            it.apply { removeLastOrNull() }
        }
    }

    /**
     * Reemplaza completamente el backstack con la nueva ruta.
     * Útil para navegación post-login/logout donde no queremos que el usuario pueda volver atrás.
     */
    fun replaceWith(route: RouteApp) {
        _backstack.update { mutableListOf(route) }
    }

    fun navTo(
        route: RouteApp
    ) {
        if (_backstack.value.last() == route) return
        if (_backstack.value.contains(route)) {
            _backstack.update { navKeys -> navKeys.apply { dropLastWhile { it != route } } }
            return
        }
        if (_backstack.value.isEmpty()) return
        _backstack.update { it.apply { add(route) } }
    }
}