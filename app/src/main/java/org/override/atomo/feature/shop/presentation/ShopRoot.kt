package org.override.atomo.feature.shop.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Shop
import org.override.atomo.feature.shop.presentation.components.ShopShimmer

@Composable
fun ShopRoot(
    viewModel: ShopViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ShopScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ShopScreen(
    state: ShopState,
    onAction: (ShopAction) -> Unit,
) {
    AtomoScaffold(
        topBar = {
            Text(
                text = "My Shops",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(ShopAction.CreateShop) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Shop")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.padding(paddingValues)) {
                ShopShimmer()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(state.shops) { shop ->
                    ShopItem(shop = shop, onAction = onAction)
                }
            }
        }
    }
}

@Composable
fun ShopItem(
    shop: Shop,
    onAction: (ShopAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(ShopAction.OpenShop(shop.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = shop.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (shop.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(ShopAction.DeleteShop(shop.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Shop")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        ShopScreen(
            state = ShopState(),
            onAction = {}
        )
    }
}