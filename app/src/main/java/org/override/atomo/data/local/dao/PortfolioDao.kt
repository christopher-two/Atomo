package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.PortfolioEntity
import org.override.atomo.data.local.entity.PortfolioItemEntity

@Dao
interface PortfolioDao {
    
    // Portfolio operations
    @Query("SELECT * FROM portfolios WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPortfoliosFlow(userId: String): Flow<List<PortfolioEntity>>
    
    @Query("SELECT * FROM portfolios WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getPortfolios(userId: String): List<PortfolioEntity>
    
    @Query("SELECT * FROM portfolios WHERE id = :portfolioId")
    suspend fun getPortfolio(portfolioId: String): PortfolioEntity?
    
    @Query("SELECT * FROM portfolios WHERE id = :portfolioId")
    fun getPortfolioFlow(portfolioId: String): Flow<PortfolioEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolios(portfolios: List<PortfolioEntity>)
    
    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)
    
    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEntity)
    
    @Query("DELETE FROM portfolios WHERE id = :portfolioId")
    suspend fun deletePortfolioById(portfolioId: String)
    
    // Portfolio Item operations
    @Query("SELECT * FROM portfolio_items WHERE portfolioId = :portfolioId ORDER BY sortOrder ASC")
    fun getItemsFlow(portfolioId: String): Flow<List<PortfolioItemEntity>>
    
    @Query("SELECT * FROM portfolio_items WHERE portfolioId = :portfolioId ORDER BY sortOrder ASC")
    suspend fun getItems(portfolioId: String): List<PortfolioItemEntity>
    
    @Query("SELECT * FROM portfolio_items WHERE id = :itemId")
    suspend fun getItem(itemId: String): PortfolioItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PortfolioItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PortfolioItemEntity>)
    
    @Update
    suspend fun updateItem(item: PortfolioItemEntity)
    
    @Delete
    suspend fun deleteItem(item: PortfolioItemEntity)
    
    @Query("DELETE FROM portfolio_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)
}
