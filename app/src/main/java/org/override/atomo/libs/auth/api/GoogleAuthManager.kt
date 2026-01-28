/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.auth.api

import android.content.Context

interface GoogleAuthManager {
    /**
     * Realiza el login con Google y devuelve el resultado de Supabase.
     * Requiere Activity Context para mostrar el diálogo.
     */
    suspend fun signIn(activityContext: Context): Result<ExternalAuthResult>

    suspend fun signOut()
}