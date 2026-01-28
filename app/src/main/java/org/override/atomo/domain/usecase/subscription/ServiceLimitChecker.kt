package org.override.atomo.domain.usecase.subscription

import kotlinx.coroutines.flow.firstOrNull
import org.override.atomo.domain.model.Plan
import org.override.atomo.domain.repository.CvRepository
import org.override.atomo.domain.repository.InvitationRepository
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.PortfolioRepository
import org.override.atomo.domain.repository.ShopRepository
import org.override.atomo.domain.repository.SubscriptionRepository
import org.override.atomo.domain.model.ServiceType

/**
 * Data class representing service limits based on subscription plan.
 * 
 * Plan limits:
 * - FREE: 1 total service
 * - CORE: 2 total services
 * - QUANTUM: Unlimited services
 * 
 * All plans have a limit of 1 per service type (only 1 menu, 1 CV, etc.)
 */
data class ServiceLimits(
    val maxPerType: Int = 1, // Always 1 - user can only have one of each type
    val maxTotalServices: Int // FREE=1, CORE=2, QUANTUM=unlimited (-1)
) {
    val isUnlimited: Boolean get() = maxTotalServices == -1
}

/**
 * Result of checking if a user can create a service.
 */
sealed interface CanCreateResult {
    data object Success : CanCreateResult
    data class ServiceTypeExists(val serviceType: ServiceType) : CanCreateResult
    data class TotalLimitReached(val currentCount: Int, val limit: Int) : CanCreateResult
    data class Error(val message: String) : CanCreateResult
}

/**
 * Gets the service limits for a given plan.
 */
class GetServiceLimitsUseCase {
    operator fun invoke(plan: Plan?): ServiceLimits {
        val planName = plan?.name?.lowercase() ?: "free"
        val maxTotal = when {
            planName.contains("quantum") -> -1 // Unlimited
            planName.contains("core") -> 2
            else -> 1 // Free plan
        }
        return ServiceLimits(maxTotalServices = maxTotal)
    }
}

/**
 * Checks if a user can create a specific service type.
 * Returns a result indicating success or the reason for failure.
 */
class CanCreateServiceUseCase(
    private val subscriptionRepository: SubscriptionRepository,
    private val menuRepository: MenuRepository,
    private val portfolioRepository: PortfolioRepository,
    private val cvRepository: CvRepository,
    private val shopRepository: ShopRepository,
    private val invitationRepository: InvitationRepository,
    private val getServiceLimits: GetServiceLimitsUseCase
) {
    suspend operator fun invoke(userId: String, serviceType: ServiceType): CanCreateResult {
        // Get current subscription and plan
        val subscription = subscriptionRepository.getSubscription(userId)
        val plan = subscription?.planId?.let { subscriptionRepository.getPlan(it) }
        val limits = getServiceLimits(plan)
        
        // Check if service type already exists (1 per type limit)
        val existingCount = getServiceCount(userId, serviceType)
        if (existingCount >= limits.maxPerType) {
            return CanCreateResult.ServiceTypeExists(serviceType)
        }
        
        // Check total service limit
        if (!limits.isUnlimited) {
            val totalServices = getTotalServiceCount(userId)
            if (totalServices >= limits.maxTotalServices) {
                return CanCreateResult.TotalLimitReached(totalServices, limits.maxTotalServices)
            }
        }
        
        return CanCreateResult.Success
    }
    
    private suspend fun getServiceCount(userId: String, serviceType: ServiceType): Int {
        return when (serviceType) {
            ServiceType.DIGITAL_MENU -> menuRepository.getMenus(userId).size
            ServiceType.PORTFOLIO -> portfolioRepository.getPortfolios(userId).size
            ServiceType.CV -> cvRepository.getCvs(userId).size
            ServiceType.SHOP -> shopRepository.getShops(userId).size
            ServiceType.INVITATION -> invitationRepository.getInvitations(userId).size
        }
    }
    
    private suspend fun getTotalServiceCount(userId: String): Int {
        return menuRepository.getMenus(userId).size +
               portfolioRepository.getPortfolios(userId).size +
               cvRepository.getCvs(userId).size +
               shopRepository.getShops(userId).size +
               invitationRepository.getInvitations(userId).size
    }
}

/**
 * Provides information about existing services for a user.
 */
class GetExistingServicesUseCase(
    private val menuRepository: MenuRepository,
    private val portfolioRepository: PortfolioRepository,
    private val cvRepository: CvRepository,
    private val shopRepository: ShopRepository,
    private val invitationRepository: InvitationRepository
) {
    suspend operator fun invoke(userId: String): Map<ServiceType, Boolean> {
        return mapOf(
            ServiceType.DIGITAL_MENU to menuRepository.getMenus(userId).isNotEmpty(),
            ServiceType.PORTFOLIO to portfolioRepository.getPortfolios(userId).isNotEmpty(),
            ServiceType.CV to cvRepository.getCvs(userId).isNotEmpty(),
            ServiceType.SHOP to shopRepository.getShops(userId).isNotEmpty(),
            ServiceType.INVITATION to invitationRepository.getInvitations(userId).isNotEmpty()
        )
    }
}
