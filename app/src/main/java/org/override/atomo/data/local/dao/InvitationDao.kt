package org.override.atomo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.override.atomo.data.local.entity.InvitationEntity
import org.override.atomo.data.local.entity.InvitationResponseEntity

@Dao
interface InvitationDao {
    
    // Invitation operations
    @Query("SELECT * FROM invitations WHERE userId = :userId ORDER BY createdAt DESC")
    fun getInvitationsFlow(userId: String): Flow<List<InvitationEntity>>
    
    @Query("SELECT * FROM invitations WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getInvitations(userId: String): List<InvitationEntity>
    
    @Query("SELECT * FROM invitations WHERE id = :invitationId")
    suspend fun getInvitation(invitationId: String): InvitationEntity?
    
    @Query("SELECT * FROM invitations WHERE id = :invitationId")
    fun getInvitationFlow(invitationId: String): Flow<InvitationEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: InvitationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitations(invitations: List<InvitationEntity>)
    
    @Update
    suspend fun updateInvitation(invitation: InvitationEntity)
    
    @Delete
    suspend fun deleteInvitation(invitation: InvitationEntity)
    
    @Query("DELETE FROM invitations WHERE id = :invitationId")
    suspend fun deleteInvitationById(invitationId: String)
    
    // Response operations
    @Query("SELECT * FROM invitation_responses WHERE invitationId = :invitationId ORDER BY createdAt DESC")
    fun getResponsesFlow(invitationId: String): Flow<List<InvitationResponseEntity>>
    
    @Query("SELECT * FROM invitation_responses WHERE invitationId = :invitationId ORDER BY createdAt DESC")
    suspend fun getResponses(invitationId: String): List<InvitationResponseEntity>
    
    @Query("SELECT COUNT(*) FROM invitation_responses WHERE invitationId = :invitationId AND status = 'confirmed'")
    suspend fun getConfirmedCount(invitationId: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: InvitationResponseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponses(responses: List<InvitationResponseEntity>)
    
    @Update
    suspend fun updateResponse(response: InvitationResponseEntity)
    
    @Delete
    suspend fun deleteResponse(response: InvitationResponseEntity)
    
    @Query("DELETE FROM invitation_responses WHERE invitationId = :invitationId")
    suspend fun deleteResponsesByInvitationId(invitationId: String)
    
    @Query("DELETE FROM invitations WHERE userId = :userId")
    suspend fun deleteAllInvitationsByUser(userId: String)
}
