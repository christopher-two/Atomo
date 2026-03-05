/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.data

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.atomo.feature.cv.data.repository.CvRepositoryImpl
import org.override.atomo.feature.cv.domain.repository.CvRepository
import org.override.atomo.feature.digital_menu.data.repository.MenuRepositoryImpl
import org.override.atomo.feature.digital_menu.domain.repository.MenuRepository
import org.override.atomo.feature.invitation.data.repository.InvitationRepositoryImpl
import org.override.atomo.feature.invitation.domain.repository.InvitationRepository
import org.override.atomo.feature.portfolio.data.repository.PortfolioRepositoryImpl
import org.override.atomo.feature.portfolio.domain.repository.PortfolioRepository
import org.override.atomo.feature.profile.data.repository.ProfileRepositoryImpl
import org.override.atomo.feature.profile.domain.repository.ProfileRepository
import org.override.atomo.feature.shop.data.repository.ShopRepositoryImpl
import org.override.atomo.feature.shop.domain.repository.ShopRepository
import org.override.atomo.feature.storage.data.repository.StorageRepositoryImpl
import org.override.atomo.feature.storage.domain.repository.StorageRepository
import org.override.atomo.feature.subscription.data.repository.SubscriptionRepositoryImpl
import org.override.atomo.feature.subscription.domain.repository.SubscriptionRepository

val RepositoryModule = module {
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
    singleOf(::MenuRepositoryImpl) bind MenuRepository::class
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    singleOf(::CvRepositoryImpl) bind CvRepository::class
    singleOf(::ShopRepositoryImpl) bind ShopRepository::class
    singleOf(::InvitationRepositoryImpl) bind InvitationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
    singleOf(::StorageRepositoryImpl) bind StorageRepository::class
}
