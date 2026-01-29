/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.portfolio

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem
import org.override.atomo.domain.repository.PortfolioRepository

/**
 * Wrapper for all Portfolio-related use cases.
 *
 * @property getPortfolios Retrieves all Portfolios for a user.
 * @property getPortfolio Retrieves a single Portfolio by ID.
 * @property syncPortfolios Synchronizes Portfolios from the backend.
 * @property createPortfolio Creates a new Portfolio.
 * @property updatePortfolio Updates an existing Portfolio.
 * @property deletePortfolio Deletes a Portfolio.
 * @property createItem Creates a portfolio item.
 * @property updateItem Updates a portfolio item.
 * @property deleteItem Deletes a portfolio item.
 */
data class PortfolioUseCases(
    val getPortfolios: GetPortfoliosUseCase,
    val getPortfolio: GetPortfolioUseCase,
    val syncPortfolios: SyncPortfoliosUseCase,
    val createPortfolio: CreatePortfolioUseCase,
    val updatePortfolio: UpdatePortfolioUseCase,
    val deletePortfolio: DeletePortfolioUseCase,
    val createItem: CreatePortfolioItemUseCase,
    val updateItem: UpdatePortfolioItemUseCase,
    val deleteItem: DeletePortfolioItemUseCase
)

/** Retrieves all portfolios for a user as a Flow. */
class GetPortfoliosUseCase(private val repository: PortfolioRepository) {
    operator fun invoke(userId: String): Flow<List<Portfolio>> = repository.getPortfoliosFlow(userId)
}

/** Retrieves a single portfolio by ID as a Flow. */
class GetPortfolioUseCase(private val repository: PortfolioRepository) {
    operator fun invoke(portfolioId: String): Flow<Portfolio?> = repository.getPortfolioFlow(portfolioId)
}

/** Synchronizes portfolios from the server. */
class SyncPortfoliosUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(userId: String): Result<List<Portfolio>> = repository.syncPortfolios(userId)
}

/** Creates a new portfolio. */
class CreatePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolio: Portfolio): Result<Portfolio> = repository.createPortfolio(portfolio)
}

/** Updates an existing portfolio. */
class UpdatePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolio: Portfolio): Result<Portfolio> = repository.updatePortfolio(portfolio)
}

/** Deletes a portfolio by ID. */
class DeletePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolioId: String): Result<Unit> = repository.deletePortfolio(portfolioId)
}

/** Creates a new portfolio item. */
class CreatePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(item: PortfolioItem): Result<PortfolioItem> = repository.createItem(item)
}

/** Updates an existing portfolio item. */
class UpdatePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(item: PortfolioItem): Result<PortfolioItem> = repository.updateItem(item)
}

/** Deletes a portfolio item used by ID. */
class DeletePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(itemId: String): Result<Unit> = repository.deleteItem(itemId)
}
