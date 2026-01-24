package org.override.atomo.feature.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import org.override.atomo.core.common.RouteMain

class HomeNavigation() {
    var currentTab by mutableStateOf(AppTab.MENU)
        private set

    private val stacks = mapOf(
        AppTab.MENU to mutableStateListOf<NavKey>(RouteMain.Menu),
        AppTab.PROFILE to mutableStateListOf<NavKey>(RouteMain.Profile)
    )

    val currentStack: List<NavKey>
        get() = stacks[currentTab] ?: emptyList()


    fun switchTab(tab: AppTab) {
        currentTab = tab
    }

    fun navigateTo(route: NavKey) {
        stacks[currentTab]?.add(route)
    }

    fun clear() {
        stacks[currentTab]?.clear()
    }

    fun back(): Boolean {
        val activeStack = stacks[currentTab] ?: return false

        if (activeStack.size > 1) {
            activeStack.remove(activeStack[activeStack.size - 1])
            return true
        }

        if (currentTab != AppTab.PROFILE) {
            currentTab = AppTab.PROFILE
            return true
        }

        return false
    }
}