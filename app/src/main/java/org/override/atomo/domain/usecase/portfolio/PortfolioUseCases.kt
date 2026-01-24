package org.override.atomo.domain.usecase.portfolio

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem
import org.override.atomo.domain.repository.PortfolioRepository

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

class GetPortfoliosUseCase(private val repository: PortfolioRepository) {
    operator fun invoke(userId: String): Flow<List<Portfolio>> = repository.getPortfoliosFlow(userId)
}

class GetPortfolioUseCase(private val repository: PortfolioRepository) {
    operator fun invoke(portfolioId: String): Flow<Portfolio?> = repository.getPortfolioFlow(portfolioId)
}

class SyncPortfoliosUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(userId: String): Result<List<Portfolio>> = repository.syncPortfolios(userId)
}

class CreatePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolio: Portfolio): Result<Portfolio> = repository.createPortfolio(portfolio)
}

class UpdatePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolio: Portfolio): Result<Portfolio> = repository.updatePortfolio(portfolio)
}

class DeletePortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(portfolioId: String): Result<Unit> = repository.deletePortfolio(portfolioId)
}

class CreatePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(item: PortfolioItem): Result<PortfolioItem> = repository.createItem(item)
}

class UpdatePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(item: PortfolioItem): Result<PortfolioItem> = repository.updateItem(item)
}

class DeletePortfolioItemUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(itemId: String): Result<Unit> = repository.deleteItem(itemId)
}
