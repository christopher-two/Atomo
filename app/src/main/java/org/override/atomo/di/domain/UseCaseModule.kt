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

val UseCaseModule = module {
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
    singleOf(::UpdateDishUseCase)
    singleOf(::DeleteDishUseCase)
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
}
