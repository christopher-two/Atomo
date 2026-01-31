/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

/**
 * Implementation of [InvitationRepository] using [InvitationDao] and [SupabaseClient].
 */
class InvitationRepositoryImpl(
    private val invitationDao: InvitationDao,
    private val supabase: SupabaseClient
) : InvitationRepository {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getInvitationsFlow(userId: String): Flow<List<Invitation>> {
        return invitationDao.getInvitationsFlow(userId).flatMapLatest { invitationEntities ->
            if (invitationEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    invitationEntities.map { invitation ->
                        invitationDao.getResponsesFlow(invitation.id).map { responses ->
                            invitation.toDomain().copy(responses = responses.map { it.toDomain() })
                        }
                    }
                ) { it.toList() }
            }
        }
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
        
        // Get current local invitation IDs to detect deleted invitations
        val localInvitationIds = invitationDao.getInvitations(userId).map { it.id }.toSet()
        val remoteInvitationIds = dtos.map { it.id }.toSet()
        
        // Delete invitations that exist locally but not on server
        val deletedInvitationIds = localInvitationIds - remoteInvitationIds
        deletedInvitationIds.forEach { invitationId ->
            invitationDao.deleteResponsesByInvitationId(invitationId)
            invitationDao.deleteInvitationById(invitationId)
        }
        
        // Insert/update invitations from server
        val entities = dtos.map { it.toEntity() }
        invitationDao.insertInvitations(entities)
        
        // Sync responses for each invitation (clear old data first)
        dtos.forEach { dto ->
            // Clear old responses for this invitation
            invitationDao.deleteResponsesByInvitationId(dto.id)
            
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
        supabase.from("invitations").update(invitation.toDto()) {
            filter { eq("id", invitation.id) }
        }
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
        supabase.from("invitation_responses").update(response) {
            filter { eq("id", response.id) }
        }
        invitationDao.updateResponse(response.toEntity())
        response
    }

    override suspend fun syncUp(userId: String): Result<Unit> = runCatching {
        // InvitationRepository syncs immediately on create/update operations
        // No optimistic updates with isSynced flag, so nothing to sync
    }
}
