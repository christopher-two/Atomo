/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.onboarding

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.override.atomo.domain.repository.CvRepository
import org.override.atomo.domain.repository.InvitationRepository
import org.override.atomo.domain.repository.MenuRepository
import org.override.atomo.domain.repository.PortfolioRepository
import org.override.atomo.domain.repository.ProfileRepository
import org.override.atomo.domain.repository.ShopRepository

/**
 * Use case that determines whether the onboarding flow should be shown.
 *
 * The onboarding is required when:
 * - The user's profile is incomplete (displayName is null/blank), OR
 * - The user has no active services (no shops, menus, portfolios, CVs, or invitations)
 *
 * This use case syncs data from the server first to ensure accurate results.
 */
class ShouldShowOnboardingUseCase(
    private val profileRepository: ProfileRepository,
    private val shopRepository: ShopRepository,
    private val menuRepository: MenuRepository,
    private val portfolioRepository: PortfolioRepository,
    private val cvRepository: CvRepository,
    private val invitationRepository: InvitationRepository
) {
    /**
     * Checks if onboarding should be shown for the given user.
     * First syncs profile and services from the server in parallel, then checks local data.
     *
     * @param userId The ID of the current user.
     * @return Flow emitting true if onboarding should be shown, false otherwise.
     */
    operator fun invoke(userId: String): Flow<Boolean> = flow {
        // Sync all data in parallel to minimize delay
        coroutineScope {
            val profileDeferred = async { profileRepository.syncProfile(userId) }
            val shopsDeferred = async { shopRepository.syncShops(userId) }
            val menusDeferred = async { menuRepository.syncMenus(userId) }
            val portfoliosDeferred = async { portfolioRepository.syncPortfolios(userId) }
            val cvsDeferred = async { cvRepository.syncCvs(userId) }
            val invitationsDeferred = async { invitationRepository.syncInvitations(userId) }

            // Await all syncs
            profileDeferred.await()
            shopsDeferred.await()
            menusDeferred.await()
            portfoliosDeferred.await()
            cvsDeferred.await()
            invitationsDeferred.await()
        }

        // Now check from local DB (which should be populated from sync)
        val profile = profileRepository.getProfileFlow(userId).firstOrNull()
        val isProfileIncomplete = profile == null || profile.displayName.isNullOrBlank()

        // Check if user has any services
        val shops = shopRepository.getShopsFlow(userId).firstOrNull() ?: emptyList()
        val menus = menuRepository.getMenusFlow(userId).firstOrNull() ?: emptyList()
        val portfolios = portfolioRepository.getPortfoliosFlow(userId).firstOrNull() ?: emptyList()
        val cvs = cvRepository.getCvsFlow(userId).firstOrNull() ?: emptyList()
        val invitations =
            invitationRepository.getInvitationsFlow(userId).firstOrNull() ?: emptyList()

        val hasNoServices = shops.isEmpty() && menus.isEmpty() &&
                portfolios.isEmpty() && cvs.isEmpty() && invitations.isEmpty()

        emit(isProfileIncomplete || hasNoServices)
    }
}


