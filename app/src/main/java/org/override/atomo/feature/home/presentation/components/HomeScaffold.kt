package org.override.atomo.feature.home.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.feature.home.presentation.HomeAction
import org.override.atomo.feature.home.presentation.HomeState
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.wrapper.WrapperHomeNavigation

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
    val isLayoutType by remember { mutableStateOf(layoutType == NavigationSuiteType.NavigationBar) }
    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            AppTab.entries.forEach {
                item(
                    selected = state.currentTab == it,
                    onClick = {
                        onAction(HomeAction.SwitchTab(it))
                    },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = if (isLayoutType) it.label else null
                        )
                    },
                    label = {
                        Text(text = it.label)
                    },
                )
            }
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    Crossfade(
                        targetState = isLayoutType,
                        label = "top_bar_transition"
                    ) { isNavigationBar ->
                        if (!isNavigationBar) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = state.currentTab.label,
                                        style = typography.titleLarge
                                    )
                                },
                                actions = {
                                    IconButton(onClick = { onAction(HomeAction.NavigateToSettings) }) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings"
                                        )
                                    }
                                }
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarManager.snackbarHostState) },
                floatingActionButton = {
                    if (state.currentTab == AppTab.DASHBOARD && isLayoutType)
                        ExpandableFab(
                            expanded = state.isFabExpanded,
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
                    WrapperHomeNavigation()
                }
            }
        }
    )
}
