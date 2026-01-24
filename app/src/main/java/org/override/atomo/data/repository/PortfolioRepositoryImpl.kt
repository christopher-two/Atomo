package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.PortfolioDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.PortfolioDto
import org.override.atomo.data.remote.dto.PortfolioItemDto
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem
import org.override.atomo.domain.repository.PortfolioRepository

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val supabase: SupabaseClient
) : PortfolioRepository {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getPortfoliosFlow(userId: String): Flow<List<Portfolio>> {
        return portfolioDao.getPortfoliosFlow(userId).flatMapLatest { portfolioEntities ->
            if (portfolioEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    portfolioEntities.map { portfolio ->
                        portfolioDao.getItemsFlow(portfolio.id).map { items ->
                            portfolio.toDomain().copy(items = items.map { it.toDomain() })
                        }
                    }
                ) { it.toList() }
            }
        }
    }
    
    override suspend fun getPortfolios(userId: String): List<Portfolio> {
        return portfolioDao.getPortfolios(userId).map { it.toDomain() }
    }
    
    override suspend fun getPortfolio(portfolioId: String): Portfolio? {
        val portfolio = portfolioDao.getPortfolio(portfolioId)?.toDomain() ?: return null
        val items = portfolioDao.getItems(portfolioId).map { it.toDomain() }
        return portfolio.copy(items = items)
    }
    
    override fun getPortfolioFlow(portfolioId: String): Flow<Portfolio?> {
        return combine(
            portfolioDao.getPortfolioFlow(portfolioId),
            portfolioDao.getItemsFlow(portfolioId)
        ) { portfolio, items ->
            portfolio?.toDomain()?.copy(items = items.map { it.toDomain() })
        }
    }
    
    override suspend fun syncPortfolios(userId: String): Result<List<Portfolio>> = runCatching {
        val dtos = supabase.from("portfolios")
            .select { filter { eq("user_id", userId) } }
            .decodeList<PortfolioDto>()
        
        val entities = dtos.map { it.toEntity() }
        portfolioDao.insertPortfolios(entities)
        
        dtos.forEach { dto ->
            val items = supabase.from("portfolio_items")
                .select { filter { eq("portfolio_id", dto.id) } }
                .decodeList<PortfolioItemDto>()
            portfolioDao.insertItems(items.map { it.toEntity() })
        }
        
        entities.map { it.toDomain() }
    }
    
    override suspend fun createPortfolio(portfolio: Portfolio): Result<Portfolio> = runCatching {
        supabase.from("portfolios").insert(portfolio.toDto())
        portfolioDao.insertPortfolio(portfolio.toEntity())
        portfolio
    }
    
    override suspend fun updatePortfolio(portfolio: Portfolio): Result<Portfolio> = runCatching {
        supabase.from("portfolios").update(portfolio.toDto()) {
            filter { eq("id", portfolio.id) }
        }
        portfolioDao.updatePortfolio(portfolio.toEntity())
        portfolio
    }
    
    override suspend fun deletePortfolio(portfolioId: String): Result<Unit> = runCatching {
        supabase.from("portfolios").delete { filter { eq("id", portfolioId) } }
        portfolioDao.deletePortfolioById(portfolioId)
    }
    
    override fun getItemsFlow(portfolioId: String): Flow<List<PortfolioItem>> {
        return portfolioDao.getItemsFlow(portfolioId).map { it.map { i -> i.toDomain() } }
    }
    
    override suspend fun createItem(item: PortfolioItem): Result<PortfolioItem> = runCatching {
        supabase.from("portfolio_items").insert(item.toDto())
        portfolioDao.insertItem(item.toEntity())
        item
    }
    
    override suspend fun updateItem(item: PortfolioItem): Result<PortfolioItem> = runCatching {
        supabase.from("portfolio_items").update(item.toDto()) {
            filter { eq("id", item.id) }
        }
        portfolioDao.updateItem(item.toEntity())
        item
    }
    
    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        supabase.from("portfolio_items").delete { filter { eq("id", itemId) } }
        portfolioDao.deleteItemById(itemId)
    }
}
