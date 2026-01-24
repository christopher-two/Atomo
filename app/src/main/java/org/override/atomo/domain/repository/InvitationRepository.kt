package org.override.atomo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse

interface InvitationRepository {
    fun getInvitationsFlow(userId: String): Flow<List<Invitation>>
    suspend fun getInvitations(userId: String): List<Invitation>
    suspend fun getInvitation(invitationId: String): Invitation?
    fun getInvitationFlow(invitationId: String): Flow<Invitation?>
    suspend fun syncInvitations(userId: String): Result<List<Invitation>>
    suspend fun createInvitation(invitation: Invitation): Result<Invitation>
    suspend fun updateInvitation(invitation: Invitation): Result<Invitation>
    suspend fun deleteInvitation(invitationId: String): Result<Unit>
    
    // Response operations
    fun getResponsesFlow(invitationId: String): Flow<List<InvitationResponse>>
    suspend fun getConfirmedCount(invitationId: String): Int
    suspend fun addResponse(response: InvitationResponse): Result<InvitationResponse>
    suspend fun updateResponse(response: InvitationResponse): Result<InvitationResponse>
}
