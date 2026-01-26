package org.override.atomo.feature.home.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import org.override.atomo.core.common.SnackbarManager
import org.override.atomo.feature.home.presentation.HomeAction
import org.override.atomo.feature.home.presentation.HomeState
import org.override.atomo.feature.navigation.AppTab

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalAnimationApi::class
)
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

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        navigationSuiteItems = {
            val visibleTabs = if (layoutType == NavigationSuiteType.NavigationBar) {
                listOf(AppTab.DASHBOARD, AppTab.MENU)
            } else {
                listOf(
                    AppTab.DASHBOARD, AppTab.PROFILE, AppTab.PAY, 
                    AppTab.DIGITAL_MENU, AppTab.SHOP, AppTab.CV, 
                    AppTab.PORTFOLIO, AppTab.INVITATION
                )
            }

            visibleTabs.forEach { tab ->
                item(
                    selected = state.currentTab == tab,
                    onClick = { 
                        if (tab == AppTab.MENU) {
                            onAction(HomeAction.ToggleMenu)
                        } else {
                            onAction(HomeAction.SwitchTab(tab))
                        }
                    },
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
                    // M3 Expressive Motion Implementation
                    TopBar(state, layoutType, onAction)
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
            
            // Mobile Menu Bottom Sheet
            if (state.isMenuSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { onAction(HomeAction.ToggleMenu) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "Menú Principal",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        val menuItems = listOf(
                            AppTab.PROFILE, AppTab.PAY, 
                            AppTab.DIGITAL_MENU, AppTab.SHOP, AppTab.CV, 
                            AppTab.PORTFOLIO, AppTab.INVITATION
                        )
                        
                        menuItems.forEach { tab ->
                            ListItem(
                                headlineContent = { Text(tab.label) },
                                leadingContent = { 
                                    Icon(
                                        imageVector = tab.icon, 
                                        contentDescription = tab.label,
                                        tint = if (state.currentTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ) 
                                },
                                modifier = Modifier.clickable {
                                    onAction(HomeAction.SwitchTab(tab))
                                    onAction(HomeAction.ToggleMenu)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
private fun TopBar(
    state: HomeState,
    layoutType: NavigationSuiteType,
    onAction: (HomeAction) -> Unit
) {
    TopAppBar(
        title = {
            // TRANSICIÓN 1: El texto se transforma verticalmente al cambiar de Tab
            AnimatedContent(
                targetState = state.currentTab,
                transitionSpec = {
                    // Efecto "Shared Axis Y" con resorte expresivo
                    (slideInVertically { height -> height } + fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut(
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    ).using(
                        // Evita que el tamaño corte el contenido durante la transformación
                        SizeTransform(clip = false)
                    )
                },
                label = "TitleTransition"
            ) { targetTab ->
                Text(
                    text = targetTab.label,
                    style = typography.titleLarge
                )
            }
        },
        actions = {
            if (layoutType != NavigationSuiteType.NavigationBar) {
                IconButton(onClick = { onAction(HomeAction.Refresh) }) {
                    // TRANSICIÓN 2: El icono se transforma en indicador de carga
                    AnimatedContent(
                        targetState = state.isRefreshing,
                        transitionSpec = {
                            // Efecto de escala (Scale) para transformación de iconos
                            (scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn())
                                .togetherWith(scaleOut() + fadeOut())
                        },
                        label = "IconTransition"
                    ) { isRefreshing ->
                        if (isRefreshing) {
                            CircularWavyProgressIndicator()
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            }
            IconButton(onClick = { onAction(HomeAction.NavigateToSettings) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}


