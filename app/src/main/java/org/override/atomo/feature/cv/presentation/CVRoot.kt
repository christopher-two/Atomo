package org.override.atomo.feature.cv.presentation

import androidx.compose.foundation.layout.Box
import org.override.atomo.core.ui.components.UpgradePlanScreen
import org.override.atomo.feature.cv.presentation.components.CvShimmer

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.R
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.AtomoScaffold
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Cv

@Composable
fun CVRoot(
    viewModel: CVViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CVScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CVScreen(
    state: CVState,
    onAction: (CVAction) -> Unit,
) {
    AtomoScaffold(
        topBar = {
            // TODO: Add TopAppBar if needed
            Text(
                text = "My CVs",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButton = {
            if (state.canCreate && !state.limitReached) {
                FloatingActionButton(onClick = { onAction(CVAction.CreateCv) }) {
                    Icon(Icons.Default.Add, contentDescription = "Create CV")
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.padding(paddingValues)) {
                CvShimmer()
            }
        } else {
            if (state.cvs.isEmpty() && state.limitReached) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    UpgradePlanScreen(
                        onUpgradeClick = { onAction(CVAction.UpgradePlan) }
                    )
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
                    items(state.cvs) { cv ->
                        CvItem(cv = cv, onAction = onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun CvItem(
    cv: Cv,
    onAction: (CVAction) -> Unit
) {
    AtomoCard(
        onClick = { onAction(CVAction.OpenCv(cv.id)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = cv.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (cv.isVisible) "Visible" else "Hidden",
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { onAction(CVAction.DeleteCv(cv.id)) }) {
                 Icon(Icons.Default.Delete, contentDescription = "Delete CV")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        CVScreen(
            state = CVState(),
            onAction = {}
        )
    }
}