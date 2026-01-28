/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.domain

object ProfileValidator {

    private val USERNAME_REGEX = Regex("^[a-z0-9_-]+$")

    fun isValidUsername(username: String): Boolean {
        return username.isNotEmpty() && USERNAME_REGEX.matches(username)
    }

    fun formatUsername(username: String): String {
        return username.lowercase()
    }

    fun isValidUrl(url: String): Boolean {
        return try {
            val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            java.net.URL(validUrl).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }
}
