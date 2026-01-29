/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.settings.domain.usecase

data class SettingsUseCases(
    val getSettings: GetSettingsUseCase,
    val updateAppearance: UpdateAppearanceUseCase,
    val updateNotifications: UpdateNotificationsUseCase,
    val updatePrivacy: UpdatePrivacyUseCase
)
