/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.util

import org.override.atomo.domain.model.ServiceType

object AtomoUrlGenerator {

    private const val BASE_URL = "https://atomo.click"

    /**
     * Generates the "Link in Bio" URL for a user.
     * Format: atomo.click/{username}
     */
    fun generateProfileUrl(username: String): String {
        return "$BASE_URL/$username"
    }

    /**
     * Generates the URL for a specific service.
     * Format: atomo.click/{username}/{serviceType}
     * 
     * Note: Since users are limited to 1 service of each type, 
     * we use the service type slug as the identifier for cleaner URLs.
     */
    fun generateServiceUrl(username: String, serviceType: ServiceType): String {
        val slug = when (serviceType) {
            ServiceType.DIGITAL_MENU -> "menu"
            ServiceType.PORTFOLIO -> "portfolio"
            ServiceType.CV -> "cv"
            ServiceType.SHOP -> "shop"
            ServiceType.INVITATION -> "invitation"
        }
        return "$BASE_URL/$username/$slug"
    }
}
