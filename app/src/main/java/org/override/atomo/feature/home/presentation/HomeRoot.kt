package org.override.atomo.feature.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.navigation.AppTab
import org.override.atomo.feature.navigation.wrapper.WrapperHomeNavigation

@Composable
fun HomeRoot(
    viewModel: HomeViewModel,
    snackbarManager: SnackbarManager = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        snackbarManager = snackbarManager,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    state: HomeState,
    snackbarManager: SnackbarManager,
    onAction: (HomeAction) -> Unit,
) {
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
                    IconButton(onClick = { onAction(HomeAction.NavigateToSettings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = state.currentTab == tab,
                        onClick = { onAction(HomeAction.SwitchTab(tab)) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = null // Icon only, no label
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarManager.snackbarHostState) },
        floatingActionButton = {
            if (state.currentTab == AppTab.DASHBOARD)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpandableFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    onCreateService: (ServiceType) -> Unit
) {
    val containerColor = colorScheme.primaryContainer
    val contentColor = colorScheme.onPrimaryContainer

    val items = listOf(
        FabItem(ServiceType.DIGITAL_MENU, Icons.Default.Restaurant, "Digital Menu"),
        FabItem(ServiceType.PORTFOLIO, Icons.Default.Folder, "Portfolio"),
        FabItem(ServiceType.CV, Icons.Default.Badge, "CV"),
        FabItem(ServiceType.SHOP, Icons.Default.Storefront, "Shop"),
        FabItem(ServiceType.INVITATION, Icons.Default.CardGiftcard, "Invitation")
    )

    val fabRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fab_rotation"
    )

    Box(
        modifier = Modifier
            .semantics {
                isTraversalGroup = true
                contentDescription = "Add new service menu"
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = if (expanded) "Close menu" else "Open menu",
                        action = { onToggle(); true }
                    )
                )
            }
    ) {
        FloatingActionButtonMenu(
            expanded = expanded,
            button = {
                ToggleFloatingActionButton(
                    modifier = Modifier.semantics { traversalIndex = -1f },
                    checked = expanded,
                    containerColor = { containerColor },
                    onCheckedChange = { onToggle() }
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Add
                        }
                    }
                    Icon(
                        imageVector = imageVector,
                        contentDescription = if (expanded) "Close" else "Add service",
                        modifier = Modifier.rotate(fabRotation),
                        tint = contentColor
                    )
                }
            }
        ) {
            items.forEachIndexed { index, item ->
                FloatingActionButtonMenuItem(
                    modifier = Modifier.semantics {
                        traversalIndex = (items.size - index).toFloat()
                    },
                    onClick = { onCreateService(item.type) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null
                        )
                    },
                    text = { Text(text = item.label) }
                )
            }
        }
    }
}

private data class FabItem(
    val type: ServiceType,
    val icon: ImageVector,
    val label: String
)

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        HomeScreen(
            state = HomeState(),
            snackbarManager = SnackbarManager(),
            onAction = {}
        )
    }
}