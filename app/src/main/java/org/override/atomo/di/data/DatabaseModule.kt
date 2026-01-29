/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.data

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.override.atomo.data.local.AtomoDatabase

val DatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AtomoDatabase::class.java,
            "atomo_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
    
    single { get<AtomoDatabase>().profileDao() }
    single { get<AtomoDatabase>().menuDao() }
    single { get<AtomoDatabase>().portfolioDao() }
    single { get<AtomoDatabase>().cvDao() }
    single { get<AtomoDatabase>().shopDao() }
    single { get<AtomoDatabase>().invitationDao() }
    single { get<AtomoDatabase>().subscriptionDao() }
}
