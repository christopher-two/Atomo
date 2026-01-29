/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.di.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.override.atomo.domain.usecase.cv.*
import org.override.atomo.domain.usecase.invitation.*
import org.override.atomo.domain.usecase.menu.*
import org.override.atomo.domain.usecase.portfolio.*
import org.override.atomo.domain.usecase.profile.*
import org.override.atomo.domain.usecase.shop.*
import org.override.atomo.domain.usecase.subscription.*
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase

import org.override.atomo.domain.usecase.session.CheckSessionUseCase

val UseCaseModule = module {
    // Session
    singleOf(::CheckSessionUseCase)

    // Profile
    singleOf(::GetProfileUseCase)
    singleOf(::SyncProfileUseCase)
    singleOf(::UpdateProfileUseCase)
    singleOf(::CheckUsernameAvailabilityUseCase)
    singleOf(::ProfileUseCases)
    
    // Menu
    singleOf(::GetMenusUseCase)
    singleOf(::GetMenuUseCase)
    singleOf(::SyncMenusUseCase)
    singleOf(::CreateMenuUseCase)
    singleOf(::UpdateMenuUseCase)
    singleOf(::DeleteMenuUseCase)
    singleOf(::CreateCategoryUseCase)
    singleOf(::CreateDishUseCase)
    singleOf(::UpsertDishUseCase)
    singleOf(::UpdateDishUseCase)
    singleOf(::DeleteDishUseCase)
    
    // Storage
    singleOf(::UploadDishImageUseCase)
    singleOf(::DeleteDishImageUseCase)
    singleOf(::MenuUseCases)
    
    // Portfolio
    singleOf(::GetPortfoliosUseCase)
    singleOf(::GetPortfolioUseCase)
    singleOf(::SyncPortfoliosUseCase)
    singleOf(::CreatePortfolioUseCase)
    singleOf(::UpdatePortfolioUseCase)
    singleOf(::DeletePortfolioUseCase)
    singleOf(::CreatePortfolioItemUseCase)
    singleOf(::UpdatePortfolioItemUseCase)
    singleOf(::DeletePortfolioItemUseCase)
    singleOf(::PortfolioUseCases)
    
    // CV
    singleOf(::GetCvsUseCase)
    singleOf(::GetCvUseCase)
    singleOf(::SyncCvsUseCase)
    singleOf(::CreateCvUseCase)
    singleOf(::UpdateCvUseCase)
    singleOf(::DeleteCvUseCase)
    singleOf(::AddEducationUseCase)
    singleOf(::AddExperienceUseCase)
    singleOf(::AddSkillUseCase)
    singleOf(::CvUseCases)
    
    // Shop
    singleOf(::GetShopsUseCase)
    singleOf(::GetShopUseCase)
    singleOf(::SyncShopsUseCase)
    singleOf(::CreateShopUseCase)
    singleOf(::UpdateShopUseCase)
    singleOf(::DeleteShopUseCase)
    singleOf(::CreateProductCategoryUseCase)
    singleOf(::CreateProductUseCase)
    singleOf(::UpdateProductUseCase)
    singleOf(::DeleteProductUseCase)
    singleOf(::ShopUseCases)
    
    // Invitation
    singleOf(::GetInvitationsUseCase)
    singleOf(::GetInvitationUseCase)
    singleOf(::SyncInvitationsUseCase)
    singleOf(::CreateInvitationUseCase)
    singleOf(::UpdateInvitationUseCase)
    singleOf(::DeleteInvitationUseCase)
    singleOf(::AddResponseUseCase)
    singleOf(::GetConfirmedCountUseCase)
    singleOf(::InvitationUseCases)
    
    // Subscription
    singleOf(::GetPlansUseCase)
    singleOf(::SyncPlansUseCase)
    singleOf(::GetSubscriptionUseCase)
    singleOf(::SyncSubscriptionUseCase)
    singleOf(::CancelSubscriptionUseCase)
    singleOf(::SubscriptionUseCases)
    
    // Service Limits
    singleOf(::GetServiceLimitsUseCase)
    singleOf(::CanCreateServiceUseCase)
    singleOf(::GetExistingServicesUseCase)
    
    // Sync
    // Sync
    singleOf(::SyncAllServicesUseCase)
}
