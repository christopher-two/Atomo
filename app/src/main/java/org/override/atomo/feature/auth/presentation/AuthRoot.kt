package org.override.atomo.feature.auth.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.override.atomo.R
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.feature.auth.presentation.viewmodel.AuthAction
import org.override.atomo.feature.auth.presentation.viewmodel.AuthState
import org.override.atomo.feature.auth.presentation.viewmodel.AuthViewModel

@Composable
fun AuthRoot(
    viewModel: AuthViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.value.error) {
        state.value.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    AuthScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(
    state: AuthState,
    snackbarHostState: SnackbarHostState,
    onAction: (AuthAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Crossfade(
            targetState = state.isLoading
        ) { isLoading ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(54.dp)
                    )
                } else {
                    Content(onAction)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.Content(onAction: (AuthAction) -> Unit) {
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = stringResource(id = R.string.app_name),
        style = typography.headlineLarge.copy(
            fontSize = 40.sp,
            color = colorScheme.onBackground
        )
    )
    Spacer(modifier = Modifier.weight(1f))
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ),
        onClick = { onAction(AuthAction.ContinueWithGoogle) }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.google_brands_solid_full),
            contentDescription = "Google",
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = R.string.continue_with_google))
    }
    Spacer(modifier = Modifier.weight(1f))
    TextButton(
        colors = ButtonDefaults.textButtonColors(
            contentColor = colorScheme.onBackground
        ),
        onClick = { onAction(AuthAction.OpenUrl("https://www.atomo.click/atomo/terms")) }
    ) {
        Text(
            text = "terms and conditions",
            style = typography.bodyMedium.copy(
                color = colorScheme.onBackground
            )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme(
        darkTheme = true
    ) {
        AuthScreen(
            state = AuthState(),
            snackbarHostState = SnackbarHostState(),
            onAction = {}
        )
    }
}
