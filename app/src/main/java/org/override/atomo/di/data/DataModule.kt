/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.atomo.data.manager.SyncManager
import org.override.atomo.libs.biometric.BiometricHelper
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.libs.session.impl.DataStoreSessionRepository
import org.override.atomo.libs.settings.api.SettingsRepository
import org.override.atomo.libs.settings.impl.DataStoreSettingsRepository

// Extensión para crear el DataStore de preferencias
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "atomo_preferences")

val DataModule: Module
    get() = module {
        // Proporcionar DataStore
        single<DataStore<Preferences>> { get<Context>().dataStore }
        
        singleOf(::DataStoreSessionRepository) bind SessionRepository::class
        singleOf(::DataStoreSettingsRepository) bind SettingsRepository::class
        singleOf(::BiometricHelper)
        singleOf(::SyncManager)
    }