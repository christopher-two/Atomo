/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.dashboard.presentation

sealed interface DashboardEvent {
    data class ShowSnackbar(val message: String) : DashboardEvent
    data class OpenUrl(val url: String) : DashboardEvent
    data class ShareUrl(val url: String, val text: String) : DashboardEvent
}
