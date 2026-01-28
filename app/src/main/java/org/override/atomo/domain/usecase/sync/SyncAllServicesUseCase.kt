package org.override.atomo.domain.usecase.sync

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.override.atomo.domain.usecase.cv.CvUseCases
import org.override.atomo.domain.usecase.invitation.InvitationUseCases
import org.override.atomo.domain.usecase.menu.MenuUseCases
import org.override.atomo.domain.usecase.portfolio.PortfolioUseCases
import org.override.atomo.domain.usecase.profile.ProfileUseCases
import org.override.atomo.domain.usecase.shop.ShopUseCases

class SyncAllServicesUseCase(
    private val profileUseCases: ProfileUseCases,
    private val menuUseCases: MenuUseCases,
    private val portfolioUseCases: PortfolioUseCases,
    private val cvUseCases: CvUseCases,
    private val shopUseCases: ShopUseCases,
    private val invitationUseCases: InvitationUseCases
) {
    suspend operator fun invoke(userId: String): Result<Unit> = coroutineScope {
        try {
            // Profile sync
            val profileDeferred = async { profileUseCases.syncProfile(userId) }
            
            // Services sync
            val servicesDeferred = listOf(
                async { menuUseCases.syncMenus(userId) },
                async { portfolioUseCases.syncPortfolios(userId) },
                async { cvUseCases.syncCvs(userId) },
                async { shopUseCases.syncShops(userId) },
                async { invitationUseCases.syncInvitations(userId) }
            )
            
            // Wait for profile
            profileDeferred.await().onFailure { return@coroutineScope Result.failure(it) }
            
            // Wait for services (we allow partial failures or full failure? Let's check all)
            val results = servicesDeferred.awaitAll()
            
            // Check if any critical failure? taking a lenient approach: if one fails, we might still want to show others.
            // But strict result return:
            val firstFailure = results.firstNotNullOfOrNull { it.exceptionOrNull() }
            if (firstFailure != null) {
                Result.failure(firstFailure)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
