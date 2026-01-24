package org.override.atomo.libs.auth.api

sealed class ExternalAuthResult {
    data class Success(val userId: String) : ExternalAuthResult()
    data class Error(val message: String) : ExternalAuthResult()
    object Cancelled : ExternalAuthResult()
}