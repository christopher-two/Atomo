/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.onboarding.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.override.atomo.domain.model.ServiceType
import org.override.atomo.feature.onboarding.presentation.OnboardingAction
import org.override.atomo.feature.onboarding.presentation.OnboardingState

/**
 * Content for step 2: Service type selection and naming.
 */
@Composable
fun ServiceStepContent(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Service type selector
        Text(
            text = "Tipo de servicio",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ServiceType.entries) { serviceType ->
                ServiceTypeCard(
                    serviceType = serviceType,
                    isSelected = state.selectedServiceType == serviceType,
                    onClick = { onAction(OnboardingAction.SelectServiceType(serviceType)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Service name
        if (state.selectedServiceType != null) {
            OutlinedTextField(
                value = state.serviceName,
                onValueChange = { onAction(OnboardingAction.UpdateServiceName(it)) },
                label = { Text("Nombre del ${getServiceLabel(state.selectedServiceType)}") },
                placeholder = { Text(getServicePlaceholder(state.selectedServiceType)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onAction(OnboardingAction.PreviousStep) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar"
                )
            }

            Button(
                onClick = { onAction(OnboardingAction.NextStep) },
                enabled = state.canProceed
            ) {
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun ServiceTypeCard(
    serviceType: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.size(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getServiceIcon(serviceType),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getServiceLabel(serviceType),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

private fun getServiceIcon(type: ServiceType): ImageVector = when (type) {
    ServiceType.DIGITAL_MENU -> Icons.Default.Restaurant
    ServiceType.PORTFOLIO -> Icons.Default.Work
    ServiceType.CV -> Icons.Default.Description
    ServiceType.SHOP -> Icons.Default.ShoppingCart
    ServiceType.INVITATION -> Icons.Default.Event
}

private fun getServiceLabel(type: ServiceType): String = when (type) {
    ServiceType.DIGITAL_MENU -> "Menú Digital"
    ServiceType.PORTFOLIO -> "Portafolio"
    ServiceType.CV -> "Currículum"
    ServiceType.SHOP -> "Tienda"
    ServiceType.INVITATION -> "Invitación"
}

private fun getServicePlaceholder(type: ServiceType): String = when (type) {
    ServiceType.DIGITAL_MENU -> "Ej: Mi Restaurante"
    ServiceType.PORTFOLIO -> "Ej: Proyectos de Juan"
    ServiceType.CV -> "Ej: Desarrollador Senior"
    ServiceType.SHOP -> "Ej: Mi Tienda Online"
    ServiceType.INVITATION -> "Ej: Boda de Juan y María"
}
