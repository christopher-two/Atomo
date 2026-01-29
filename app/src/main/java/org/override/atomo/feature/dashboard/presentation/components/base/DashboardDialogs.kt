/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation.components.base

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.override.atomo.feature.dashboard.presentation.DashboardAction
import org.override.atomo.feature.dashboard.presentation.DeleteDialogState

@Composable
fun DashboardDeleteDialog(
    dialogState: DeleteDialogState,
    onAction: (DashboardAction) -> Unit
) {
    val (title, message) = when (dialogState) {
        is DeleteDialogState.DeleteMenu -> "Eliminar menú" to "¿Estás seguro de que quieres eliminar el menú '${dialogState.menu.name}'? Esta acción no se puede deshacer."
        is DeleteDialogState.DeletePortfolio -> "Eliminar portfolio" to "¿Estás seguro de que quieres eliminar el portfolio '${dialogState.portfolio.title}'? Esta acción no se puede deshacer."
        is DeleteDialogState.DeleteCv -> "Eliminar currículum" to "¿Estás seguro de que quieres eliminar el CV '${dialogState.cv.title}'? Esta acción no se puede deshacer."
        is DeleteDialogState.DeleteShop -> "Eliminar tienda" to "¿Estás seguro de que quieres eliminar la tienda '${dialogState.shop.name}'? Esta acción no se puede deshacer."
        is DeleteDialogState.DeleteInvitation -> "Eliminar invitación" to "¿Estás seguro de que quieres eliminar la invitación '${dialogState.invitation.eventName}'? Esta acción no se puede deshacer."
    }

    AlertDialog(
        onDismissRequest = { onAction(DashboardAction.DismissDeleteDialog) },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = { onAction(DashboardAction.ConfirmDelete) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onAction(DashboardAction.DismissDeleteDialog) }
            ) {
                Text("Cancelar")
            }
        }
    )
}
