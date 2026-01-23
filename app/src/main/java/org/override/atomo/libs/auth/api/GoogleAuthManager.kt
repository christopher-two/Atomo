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