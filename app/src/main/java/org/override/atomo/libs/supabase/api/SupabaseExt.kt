/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.supabase.api

import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.HttpRequestException

/**
 * Extension functions for working with Supabase Results.
 */
inline fun <T> Result<T>.mapSupabaseError(): Result<T> {
    return onFailure { e ->
        val friendlyMessage = when (e) {
            is RestException -> "Database error: ${e.message}"
            is HttpRequestException -> "Network error: check your connection"
            else -> e.message ?: "Unknown error occurred"
        }
        // You could wrap this in a custom AppError class if needed
    }
}
