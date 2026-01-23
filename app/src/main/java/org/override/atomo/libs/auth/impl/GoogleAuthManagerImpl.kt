package org.override.atomo.libs.auth.impl

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import org.override.atomo.R
import org.override.atomo.libs.auth.api.ExternalAuthResult
import org.override.atomo.libs.auth.api.GoogleAuthManager
import java.security.MessageDigest
import java.util.UUID

class GoogleAuthManagerImpl(
    private val context: Context,
    private val supabase: SupabaseClient,
) : GoogleAuthManager {
    companion object {
        private const val TAG = "GoogleAuthManager"
    }

    // Inicializamos el Credential Manager
    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(activityContext: Context): Result<ExternalAuthResult> {
        return try {
            // 1. Generar un nonce para seguridad (evita ataques de repetición)
            val hashedNonce = getHashedNonce()

            // 2. Configurar la opción de Google ID
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // true si solo quieres cuentas ya usadas antes
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce) // Opcional pero recomendado con Firebase
                .build()

            // 3. Crear la petición
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // 4. Llamar al Credential Manager (esto muestra el UI de Google al usuario)
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            // 5. Procesar la credencial recibida
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                // 2. La convertimos manualmente usando el helper de Google
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(result.credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken
                    supabase.auth.signInWith(IDToken) {
                        idToken = googleIdToken
                        provider = Google
                        nonce = hashedNonce
                    }

                    Result.success(ExternalAuthResult.Success)

                } catch (e: Exception) {
                    Log.e("GoogleAuth", "Error al convertir la credencial", e)
                    Result.failure(e)
                }

            } else {
                // Log para depurar qué tipo de credencial llegó si no es la esperada
                Log.e(
                    "GoogleAuth",
                    "Tipo recibido desconocido: ${credential.type} / Clase: ${credential.javaClass.name}"
                )
                Result.failure(Exception("Tipo de credencial no reconocido: ${credential.type}"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error en Credential Manager: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error general durante el sign-in", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try {
            // 1. Cerrar sesión en Firebase Auth
            supabase.auth.signOut()

            // 2. Limpiar el estado de las credenciales del Credential Manager
            credentialManager.clearCredentialState(
                request = ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al cerrar sesión", e)
        }
    }

    // Métodos auxiliares de seguridad
    private fun getHashedNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}