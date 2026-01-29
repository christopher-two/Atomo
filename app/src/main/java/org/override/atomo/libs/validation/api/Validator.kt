/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.libs.validation.api

interface Validator<T> {
    fun validate(input: T): ValidationResult
}

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Error(val message: String) : ValidationResult
    
    val isValid: Boolean get() = this is Success
}

object CommonValidators {
    fun required(message: String = "Field is required"): Validator<String?> = object : Validator<String?> {
        override fun validate(input: String?): ValidationResult {
            return if (input.isNullOrBlank()) ValidationResult.Error(message) else ValidationResult.Success
        }
    }
    
    fun email(message: String = "Invalid email"): Validator<String?> = object : Validator<String?> {
        override fun validate(input: String?): ValidationResult {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            return if (input != null && emailRegex.matches(input)) ValidationResult.Success else ValidationResult.Error(message)
        }
    }
}
