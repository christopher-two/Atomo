package org.christophertwo.spot.libs.auth.api

import android.content.Context
import com.google.firebase.auth.AuthResult

interface GoogleAuthManager {
    /**
     * Realiza el login con Google y devuelve el resultado de Firebase.
     * Requiere Activity Context para mostrar el diálogo.
     */
    suspend fun signIn(activityContext: Context): Result<AuthResult>

    suspend fun signOut()
}