package org.override.atomo.libs.auth.api

sealed class ExternalAuthResult {
    object Success : ExternalAuthResult()
    data class Error(val message: String) : ExternalAuthResult()
    object Cancelled : ExternalAuthResult()
}