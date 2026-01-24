package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.override.atomo.data.local.dao.InvitationDao
import org.override.atomo.data.mapper.toDomain
import org.override.atomo.data.mapper.toDto
import org.override.atomo.data.mapper.toEntity
import org.override.atomo.data.remote.dto.InvitationDto
import org.override.atomo.data.remote.dto.InvitationResponseDto
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse
import org.override.atomo.domain.repository.InvitationRepository

class InvitationRepositoryImpl(
    private val invitationDao: InvitationDao,
    private val supabase: SupabaseClient
) : InvitationRepository {
    
    override fun getInvitationsFlow(userId: String): Flow<List<Invitation>> {
        return invitationDao.getInvitationsFlow(userId).map { it.map { i -> i.toDomain() } }
    }
    
    override suspend fun getInvitations(userId: String): List<Invitation> {
        return invitationDao.getInvitations(userId).map { it.toDomain() }
    }
    
    override suspend fun getInvitation(invitationId: String): Invitation? {
        val invitation = invitationDao.getInvitation(invitationId)?.toDomain() ?: return null
        val responses = invitationDao.getResponses(invitationId).map { it.toDomain() }
        return invitation.copy(responses = responses)
    }
    
    override fun getInvitationFlow(invitationId: String): Flow<Invitation?> {
        return combine(
            invitationDao.getInvitationFlow(invitationId),
            invitationDao.getResponsesFlow(invitationId)
        ) { invitation, responses ->
            invitation?.toDomain()?.copy(responses = responses.map { it.toDomain() })
        }
    }
    
    override suspend fun syncInvitations(userId: String): Result<List<Invitation>> = runCatching {
        val dtos = supabase.from("invitations")
            .select { filter { eq("user_id", userId) } }
            .decodeList<InvitationDto>()
        
        val entities = dtos.map { it.toEntity() }
        invitationDao.insertInvitations(entities)
        
        dtos.forEach { dto ->
            val responses = supabase.from("invitation_responses")
                .select { filter { eq("invitation_id", dto.id) } }
                .decodeList<InvitationResponseDto>()
            invitationDao.insertResponses(responses.map { it.toEntity() })
        }
        
        entities.map { it.toDomain() }
    }
    
    override suspend fun createInvitation(invitation: Invitation): Result<Invitation> = runCatching {
        supabase.from("invitations").insert(invitation.toDto())
        invitationDao.insertInvitation(invitation.toEntity())
        invitation
    }
    
    override suspend fun updateInvitation(invitation: Invitation): Result<Invitation> = runCatching {
        supabase.from("invitations").upsert(invitation.toDto())
        invitationDao.updateInvitation(invitation.toEntity())
        invitation
    }
    
    override suspend fun deleteInvitation(invitationId: String): Result<Unit> = runCatching {
        supabase.from("invitations").delete { filter { eq("id", invitationId) } }
        invitationDao.deleteInvitationById(invitationId)
    }
    
    override fun getResponsesFlow(invitationId: String): Flow<List<InvitationResponse>> {
        return invitationDao.getResponsesFlow(invitationId).map { it.map { r -> r.toDomain() } }
    }
    
    override suspend fun getConfirmedCount(invitationId: String): Int {
        return invitationDao.getConfirmedCount(invitationId)
    }
    
    override suspend fun addResponse(response: InvitationResponse): Result<InvitationResponse> = runCatching {
        supabase.from("invitation_responses").insert(response)
        invitationDao.insertResponse(response.toEntity())
        response
    }
    
    override suspend fun updateResponse(response: InvitationResponse): Result<InvitationResponse> = runCatching {
        supabase.from("invitation_responses").upsert(response)
        invitationDao.updateResponse(response.toEntity())
        response
    }
}
