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
import org.override.atomo.domain.usecase.cv.AddEducationUseCase
import org.override.atomo.domain.usecase.cv.AddExperienceUseCase
import org.override.atomo.domain.usecase.cv.AddSkillUseCase
import org.override.atomo.domain.usecase.cv.CreateCvUseCase
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.cv.DeleteCvUseCase
import org.override.atomo.domain.usecase.cv.GetCvUseCase
import org.override.atomo.domain.usecase.cv.GetCvsUseCase
import org.override.atomo.domain.usecase.cv.SyncCvsUseCase
import org.override.atomo.domain.usecase.cv.UpdateCvUseCase
import org.override.atomo.domain.usecase.invitation.AddResponseUseCase
import org.override.atomo.domain.usecase.invitation.CreateInvitationUseCase
import org.override.atomo.domain.usecase.invitation.DeleteInvitationUseCase
import org.override.atomo.domain.usecase.invitation.GetConfirmedCountUseCase
import org.override.atomo.domain.usecase.invitation.GetInvitationUseCase
import org.override.atomo.domain.usecase.invitation.GetInvitationsUseCase
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.invitation.SyncInvitationsUseCase
import org.override.atomo.domain.usecase.invitation.UpdateInvitationUseCase
import org.override.atomo.domain.usecase.menu.CreateCategoryUseCase
import org.override.atomo.domain.usecase.menu.CreateDishUseCase
import org.override.atomo.domain.usecase.menu.CreateMenuUseCase
import org.override.atomo.domain.usecase.menu.DeleteCategoryUseCase
import org.override.atomo.domain.usecase.menu.DeleteDishUseCase
import org.override.atomo.domain.usecase.menu.DeleteMenuUseCase
import org.override.atomo.domain.usecase.menu.GetMenuUseCase
import org.override.atomo.domain.usecase.menu.GetMenusUseCase
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.menu.SyncMenusUseCase
import org.override.atomo.domain.usecase.menu.UpdateCategoryUseCase
import org.override.atomo.domain.usecase.menu.UpdateDishUseCase
import org.override.atomo.domain.usecase.menu.UpdateMenuUseCase
import org.override.atomo.domain.usecase.menu.UpsertDishUseCase
import org.override.atomo.domain.usecase.portfolio.CreatePortfolioItemUseCase
import org.override.atomo.domain.usecase.portfolio.CreatePortfolioUseCase
import org.override.atomo.domain.usecase.portfolio.DeletePortfolioItemUseCase
import org.override.atomo.domain.usecase.portfolio.DeletePortfolioUseCase
import org.override.atomo.domain.usecase.portfolio.GetPortfolioUseCase
import org.override.atomo.domain.usecase.portfolio.GetPortfoliosUseCase
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.portfolio.SyncPortfoliosUseCase
import org.override.atomo.domain.usecase.portfolio.UpdatePortfolioItemUseCase
import org.override.atomo.domain.usecase.portfolio.UpdatePortfolioUseCase
import org.override.atomo.domain.usecase.profile.CheckUsernameAvailabilityUseCase
import org.override.atomo.domain.usecase.profile.GetProfileUseCase
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.profile.SyncProfileUseCase
import org.override.atomo.domain.usecase.profile.UpdateProfileUseCase
import org.override.atomo.domain.usecase.session.CheckSessionUseCase
import org.override.atomo.domain.usecase.shop.CreateProductCategoryUseCase
import org.override.atomo.domain.usecase.shop.CreateProductUseCase
import org.override.atomo.domain.usecase.shop.CreateShopUseCase
import org.override.atomo.domain.usecase.shop.DeleteProductUseCase
import org.override.atomo.domain.usecase.shop.DeleteShopUseCase
import org.override.atomo.domain.usecase.shop.GetShopUseCase
import org.override.atomo.domain.usecase.shop.GetShopsUseCase
import org.override.atomo.domain.usecase.shop.ShopUseCases
import org.override.atomo.domain.usecase.shop.SyncShopsUseCase
import org.override.atomo.domain.usecase.shop.UpdateProductUseCase
import org.override.atomo.domain.usecase.shop.UpdateShopUseCase
import org.override.atomo.domain.usecase.storage.DeleteDishImageUseCase
import org.override.atomo.domain.usecase.storage.UploadDishImageUseCase
import org.override.atomo.domain.usecase.subscription.CanAddDishUseCase
import org.override.atomo.domain.usecase.subscription.CanCreateServiceUseCase
import org.override.atomo.domain.usecase.subscription.CancelSubscriptionUseCase
import org.override.atomo.domain.usecase.subscription.GetExistingServicesUseCase
import org.override.atomo.domain.usecase.subscription.GetPlansUseCase
import org.override.atomo.domain.usecase.subscription.GetServiceLimitsUseCase
import org.override.atomo.domain.usecase.subscription.GetSubscriptionUseCase
import org.override.atomo.domain.usecase.subscription.SubscriptionUseCases
import org.override.atomo.domain.usecase.subscription.SyncPlansUseCase
import org.override.atomo.domain.usecase.subscription.SyncSubscriptionUseCase
import org.override.atomo.domain.usecase.sync.SyncAllServicesUseCase

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
    singleOf(::UpdateCategoryUseCase)
    singleOf(::DeleteCategoryUseCase)
    singleOf(::CreateDishUseCase)
    factory { UpsertDishUseCase(get(), get(), get(), get()) }
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
    singleOf(::CanAddDishUseCase)
    singleOf(::GetExistingServicesUseCase)
    
    // Sync
    // Sync
    singleOf(::SyncAllServicesUseCase)
}
