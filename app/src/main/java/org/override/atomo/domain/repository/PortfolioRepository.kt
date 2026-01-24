package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem

interface PortfolioRepository {
    fun getPortfoliosFlow(userId: String): Flow<List<Portfolio>>
    suspend fun getPortfolios(userId: String): List<Portfolio>
    suspend fun getPortfolio(portfolioId: String): Portfolio?
    fun getPortfolioFlow(portfolioId: String): Flow<Portfolio?>
    suspend fun syncPortfolios(userId: String): Result<List<Portfolio>>
    suspend fun createPortfolio(portfolio: Portfolio): Result<Portfolio>
    suspend fun updatePortfolio(portfolio: Portfolio): Result<Portfolio>
    suspend fun deletePortfolio(portfolioId: String): Result<Unit>
    
    // Item operations
    fun getItemsFlow(portfolioId: String): Flow<List<PortfolioItem>>
    suspend fun createItem(item: PortfolioItem): Result<PortfolioItem>
    suspend fun updateItem(item: PortfolioItem): Result<PortfolioItem>
    suspend fun deleteItem(itemId: String): Result<Unit>
}
