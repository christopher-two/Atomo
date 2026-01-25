package org.override.atomo.feature.home.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.feature.home.presentation.HomeAction
import org.override.atomo.feature.home.presentation.HomeState
import org.override.atomo.feature.navigation.AppTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(
    snackbarManager: SnackbarManager,
    state: HomeState,
    content: @Composable () -> Unit,
    onAction: (HomeAction) -> Unit
) {
    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }

    val layoutType = if (windowSize.width >= 1200.dp) {
        NavigationSuiteType.NavigationDrawer
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
            currentWindowAdaptiveInfo()
        )
    }

    val isNavigationBar by remember { derivedStateOf { layoutType == NavigationSuiteType.NavigationBar } }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        navigationSuiteItems = {
            AppTab.entries.forEach { tab ->
                item(
                    selected = state.currentTab == tab,
                    onClick = { onAction(HomeAction.SwitchTab(tab)) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label
                        )
                    },
                    label = { Text(text = tab.label) }
                )
            }
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = state.currentTab.label,
                                style = typography.titleLarge
                            )
                        },
                        actions = {
                            IconButton(onClick = { onAction(HomeAction.Refresh) }) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync"
                                )
                            }
                            IconButton(onClick = { onAction(HomeAction.NavigateToSettings) }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarManager.snackbarHostState) },
                floatingActionButton = {
                    if (state.currentTab == AppTab.DASHBOARD)
                        ExpandableFab(
                            expanded = state.isFabExpanded,
                            availableServiceTypes = state.availableServiceTypes,
                            onToggle = { onAction(HomeAction.ToggleFab) },
                            onCreateService = { type -> onAction(HomeAction.CreateService(type)) }
                        )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    content()
                }
            }
        }
    )
}


