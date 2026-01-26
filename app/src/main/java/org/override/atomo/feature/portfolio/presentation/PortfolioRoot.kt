package org.override.atomo.feature.portfolio.presentation

import androidx.compose.foundation.layout.Box
import org.override.atomo.feature.portfolio.presentation.components.PortfolioShimmer

import androidx.compose.foundation.layout.Arrangement
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
import org.override.atomo.domain.model.Portfolio

@Composable
fun PortfolioRoot(
    viewModel: PortfolioViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PortfolioScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PortfolioScreen(
    state: PortfolioState,
    onAction: (PortfolioAction) -> Unit,
) {
    AtomoScaffold(
        topBar = {
            Text(
                text = "My Portfolios",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(PortfolioAction.CreatePortfolio) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Portfolio")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.padding(paddingValues)) {
                PortfolioShimmer()
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
                items(state.portfolios) { portfolio ->
                    PortfolioItem(portfolio = portfolio, onAction = onAction)
                }
            }
        }
    }
}

@Composable
fun PortfolioItem(
    portfolio: Portfolio,
    onAction: (PortfolioAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(PortfolioAction.OpenPortfolio(portfolio.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = portfolio.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (portfolio.isVisible) "Visible" else "Hidden",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(PortfolioAction.DeletePortfolio(portfolio.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete Portfolio")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        PortfolioScreen(
            state = PortfolioState(),
            onAction = {}
        )
    }
}