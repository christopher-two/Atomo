/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem

/**
 * Repository interface for managing Portfolios and Items.
 */
interface PortfolioRepository {
    /** Retrieves all portfolios for a user as a Flow. */
    fun getPortfoliosFlow(userId: String): Flow<List<Portfolio>>

    /** Retrieves all portfolios for a user (suspend). */
    suspend fun getPortfolios(userId: String): List<Portfolio>

    /** Retrieves a single portfolio by ID (suspend). */
    suspend fun getPortfolio(portfolioId: String): Portfolio?

    /** Retrieves a single portfolio by ID as a Flow. */
    fun getPortfolioFlow(portfolioId: String): Flow<Portfolio?>

    /** Synchronizes portfolios from the remote source. */
    suspend fun syncPortfolios(userId: String): Result<List<Portfolio>>

    /** Creates a new portfolio. */
    suspend fun createPortfolio(portfolio: Portfolio): Result<Portfolio>

    /** Updates an existing portfolio. */
    suspend fun updatePortfolio(portfolio: Portfolio): Result<Portfolio>

    /** Deletes a portfolio. */
    suspend fun deletePortfolio(portfolioId: String): Result<Unit>
    
    // Item operations

    /** Retrieves items for a portfolio as a Flow. */
    fun getItemsFlow(portfolioId: String): Flow<List<PortfolioItem>>

    /** Creates a new portfolio item. */
    suspend fun createItem(item: PortfolioItem): Result<PortfolioItem>

    /** Updates a portfolio item. */
    suspend fun updateItem(item: PortfolioItem): Result<PortfolioItem>

    /** Deletes a portfolio item. */
    suspend fun deleteItem(itemId: String): Result<Unit>
}
