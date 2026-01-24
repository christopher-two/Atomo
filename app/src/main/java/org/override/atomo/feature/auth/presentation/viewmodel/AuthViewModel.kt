package org.override.atomo.feature.auth.presentation.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.override.atomo.core.common.RouteApp
import org.override.atomo.feature.auth.domain.usecase.ContinueWithGoogleUseCase
import org.override.atomo.feature.auth.domain.usecase.SaveUserSessionUseCase
import org.override.atomo.feature.navigation.RootNavigation
import org.override.atomo.libs.auth.api.ExternalAuthResult

class AuthViewModel(
    private val continueWithGoogleUseCase: ContinueWithGoogleUseCase,
    private val saveUserSessionUseCase: SaveUserSessionUseCase,
    private val rootNavigation: RootNavigation,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onAction(action: AuthAction, ctx: Context) {
        when (action) {
            AuthAction.ContinueWithGoogle -> continueWithGoogle(ctx)
            is AuthAction.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, action.url.toUri())
                ctx.startActivity(intent)
            }
        }
    }

    private fun continueWithGoogle(ctx: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                continueWithGoogleUseCase(ctx).fold(
                    onSuccess = { result ->
                        when (result) {
                            is ExternalAuthResult.Success -> {
                                // Guardar la sesión del usuario
                                saveUserSessionUseCase(result.userId)
                                rootNavigation.replaceWith(RouteApp.Home)
                            }

                            is ExternalAuthResult.Error -> {
                                _state.update { it.copy(error = result.message) }
                            }

                            ExternalAuthResult.Cancelled -> {
                                // Usuario canceló, no hacer nada
                            }
                        }
                    },
                    onFailure = { exception ->
                        _state.update { it.copy(error = exception.message) }
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}