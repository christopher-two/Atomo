package org.override.atomo.feature.settings.presentation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.feature.settings.presentation.components.SettingsDropdown
import org.override.atomo.feature.settings.presentation.components.SettingsSection
import org.override.atomo.feature.settings.presentation.components.SettingsSlider
import org.override.atomo.feature.settings.presentation.components.SettingsSwitch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsRoot(
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(SettingsAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Appearance Section
                SettingsSection(title = "Appearance") {
                    SettingsSwitch(
                        title = "Dark Mode",
                        checked = state.appearance.isDarkModeEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleDarkMode(it)) },
                        description = "Enable dark mode for a darker appearance, easier on the eyes in low light."
                    )
                    SettingsDropdown(
                        title = "Theme",
                        options = listOf("auto", "pink", "blue", "green", "purple"),
                        selectedOption = state.appearance.theme,
                        onOptionSelected = { viewModel.onAction(SettingsAction.SetTheme(it)) },
                        description = "Choose the base color theme for the application manually."
                    )
                    SettingsSwitch(
                        title = "Dynamic Color",
                        checked = state.appearance.isDynamicColorEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleDynamicColor(it)) },
                        description = "Use your wallpaper's colors to theme the app (Android 12+)."
                    )
                    SettingsSwitch(
                        title = "System Theme",
                        checked = state.appearance.isSystemThemeEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleSystemTheme(it)) },
                        description = "Follow the system's Light/Dark mode setting automatically."
                    )
                }

                // Notifications Section
                SettingsSection(title = "Notifications") {
                    SettingsSwitch(
                        title = "Enable Notifications",
                        checked = state.notifications.areNotificationsEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleNotifications(it)) },
                        description = "Receive push notifications from the app."
                    )
                    SettingsSwitch(
                        title = "Notification Sound",
                        checked = state.notifications.isNotificationSoundEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleNotificationSound(it)) },
                        description = "Play a sound when a notification is received."
                    )
                    SettingsSlider(
                        title = "Priority",
                        value = state.notifications.notificationPriority,
                        onValueChange = { viewModel.onAction(SettingsAction.SetNotificationPriority(it)) },
                        valueRange = 0f..5f,
                        steps = 4,
                        description = "Set the priority level for notifications (0 = Low, 5 = High)."
                    )
                }

                // Privacy Section
                SettingsSection(title = "Privacy") {
                    SettingsSwitch(
                        title = "Biometric Auth",
                        checked = state.privacy.isBiometricAuthEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleBiometricAuth(it)) },
                        description = "Require fingerprint or face unlock to access the app."
                    )
                    SettingsSwitch(
                        title = "Analytics",
                        checked = state.privacy.isAnalyticsEnabled,
                        onCheckedChange = { viewModel.onAction(SettingsAction.ToggleAnalytics(it)) },
                        description = "Send anonymous usage data to help us improve."
                    )
                }

                // Subscription Section
                SettingsSection(title = "Suscripción") {
                    Button(
                        onClick = { viewModel.onAction(SettingsAction.NavigateToPay) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Ver Planes de Suscripción")
                    }
                }

                // Account Actions
                SettingsSection(title = "Account") {
                     Button(
                        onClick = { viewModel.onAction(SettingsAction.Logout) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}