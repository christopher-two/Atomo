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
import androidx.work.WorkManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.atomo.feature.sync.data.manager.SyncManager
import org.override.atomo.feature.biometric.presentation.BiometricHelper
import org.override.atomo.feature.session.domain.repository.SessionRepository
import org.override.atomo.feature.session.data.repository.DataStoreSessionRepository
import org.override.atomo.feature.settings.domain.repository.SettingsRepository
import org.override.atomo.feature.settings.data.repository.DataStoreSettingsRepository

// Extensión para crear el DataStore de preferencias
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "atomo_preferences")

val DataModule: Module
    get() = module {
        // Proporcionar DataStore
        single<DataStore<Preferences>> { get<Context>().dataStore }

        // Proporcionar WorkManager
        single { WorkManager.getInstance(get()) }
        
        singleOf(::DataStoreSessionRepository) bind SessionRepository::class
        singleOf(::DataStoreSettingsRepository) bind SettingsRepository::class
        singleOf(::BiometricHelper)
        singleOf(::SyncManager)
    }