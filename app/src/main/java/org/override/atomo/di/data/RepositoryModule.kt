package org.override.atomo.di.data

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.override.atomo.data.repository.CvRepositoryImpl
import org.override.atomo.data.repository.InvitationRepositoryImpl
import org.override.atomo.data.repository.MenuRepositoryImpl
import org.override.atomo.data.repository.PortfolioRepositoryImpl
import org.override.atomo.data.repository.ProfileRepositoryImpl
import org.override.atomo.data.repository.ShopRepositoryImpl
import org.override.atomo.data.repository.SubscriptionRepositoryImpl
import org.override.atomo.domain.repository.CvRepository
import org.override.atomo.domain.repository.InvitationRepository
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.PortfolioRepository
import org.override.atomo.domain.repository.ProfileRepository
import org.override.atomo.domain.repository.ShopRepository
import org.override.atomo.domain.repository.SubscriptionRepository

val RepositoryModule = module {
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
    singleOf(::MenuRepositoryImpl) bind MenuRepository::class
    singleOf(::PortfolioRepositoryImpl) bind PortfolioRepository::class
    singleOf(::CvRepositoryImpl) bind CvRepository::class
    singleOf(::ShopRepositoryImpl) bind ShopRepository::class
    singleOf(::InvitationRepositoryImpl) bind InvitationRepository::class
    singleOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
}
