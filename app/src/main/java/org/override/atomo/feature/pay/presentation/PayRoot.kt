package org.override.atomo.feature.pay.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.override.atomo.core.ui.theme.AtomoTheme
import org.override.atomo.domain.model.Plan

@Composable
fun PayRoot(
    viewModel: PayViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PayScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    state: PayState,
    onAction: (PayAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planes de Suscripción") },
                navigationIcon = {
                    IconButton(onClick = { onAction(PayAction.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Tu plan actual: ${state.currentPlanName}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(state.plans) { plan ->
                        PlanCard(
                            plan = plan,
                            isCurrentPlan = plan.id == state.currentPlan?.id,
                            onSelect = { onAction(PayAction.SelectPlan(plan)) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            // Loading overlay
            AnimatedVisibility(
                visible = state.isOperationLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
    
    // Confirmation Dialog
    if (state.showConfirmDialog && state.selectedPlan != null) {
        AlertDialog(
            onDismissRequest = { onAction(PayAction.DismissDialog) },
            title = { Text("Confirmar Suscripción") },
            text = {
                Text("¿Deseas cambiar al plan ${state.selectedPlan.name} por $${state.selectedPlan.price.toInt()} MXN/${state.selectedPlan.interval}?")
            },
            confirmButton = {
                Button(onClick = { onAction(PayAction.ConfirmSubscription) }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(PayAction.DismissDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    isCurrentPlan: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPremium = plan.name.lowercase().contains("quantum")
    val isCore = plan.name.lowercase().contains("core")
    
    val gradientColors = when {
        isPremium -> listOf(Color(0xFF7C3AED), Color(0xFF9333EA))
        isCore -> listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
        else -> listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isCurrentPlan) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
            .clickable(enabled = !isCurrentPlan) { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPremium || isCore) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isPremium || isCore) {
                        Modifier.background(brush = Brush.linearGradient(gradientColors))
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPremium) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = plan.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium || isCore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (isCurrentPlan) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Actual",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Price
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (plan.price == 0.0) "Gratis" else "$${plan.price.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium || isCore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (plan.price > 0) {
                        Text(
                            text = " MXN/${plan.interval}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPremium || isCore) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Features
                plan.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isPremium || isCore) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPremium || isCore) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (!isCurrentPlan && plan.price > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSelect,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Seleccionar Plan")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AtomoTheme {
        PayScreen(
            state = PayState(
                isLoading = false,
                plans = listOf(
                    Plan(
                        id = "1",
                        name = "Free",
                        description = "Plan gratuito",
                        price = 0.0,
                        currency = "MXN",
                        interval = "mes",
                        features = listOf("1 servicio", "Subdominio compartido", "Con publicidad"),
                        isActive = true,
                        createdAt = 0
                    ),
                    Plan(
                        id = "2",
                        name = "Core",
                        description = "Plan intermedio",
                        price = 59.0,
                        currency = "MXN",
                        interval = "mes",
                        features = listOf("2 servicios", "URL personalizada", "Sin anuncios de terceros"),
                        isActive = true,
                        createdAt = 0
                    ),
                    Plan(
                        id = "3",
                        name = "Quantum",
                        description = "Plan profesional",
                        price = 159.0,
                        currency = "MXN",
                        interval = "mes",
                        features = listOf("Servicios ilimitados", "Dominio propio", "Sin publicidad", "Plantillas Premium"),
                        isActive = true,
                        createdAt = 0
                    )
                )
            ),
            onAction = {}
        )
    }
}