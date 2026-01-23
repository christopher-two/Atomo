package org.override.atomo.di.data

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.atomo.libs.biometric.BiometricHelper
import org.override.atomo.libs.session.api.SessionRepository
import org.override.atomo.libs.session.impl.DataStoreSessionRepository
import org.override.atomo.libs.settings.api.SettingsRepository
import org.override.atomo.libs.settings.impl.DataStoreSettingsRepository

val DataModule: Module
    get() = module {
        singleOf(::DataStoreSessionRepository) bind SessionRepository::class
        singleOf(::DataStoreSettingsRepository) bind SettingsRepository::class
        singleOf(::BiometricHelper)
    }