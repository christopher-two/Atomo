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
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse

/**
 * Repository interface for managing event Invitations and Responses.
 */
interface InvitationRepository {
    /** Retrieves all invitations for a user as a Flow. */
    fun getInvitationsFlow(userId: String): Flow<List<Invitation>>

    /** Retrieves all invitations for a user (suspend). */
    suspend fun getInvitations(userId: String): List<Invitation>

    /** Retrieves a single invitation by ID (suspend). */
    suspend fun getInvitation(invitationId: String): Invitation?

    /** Retrieves a single invitation by ID as a Flow. */
    fun getInvitationFlow(invitationId: String): Flow<Invitation?>

    /** Synchronizes invitations from the remote data source. */
    suspend fun syncInvitations(userId: String): Result<List<Invitation>>

    /** Creates a new invitation. */
    suspend fun createInvitation(invitation: Invitation): Result<Invitation>

    /** Updates an existing invitation. */
    suspend fun updateInvitation(invitation: Invitation): Result<Invitation>

    /** Deletes an invitation. */
    suspend fun deleteInvitation(invitationId: String): Result<Unit>
    
    // Response operations

    /** Retrieves responses for an invitation as a Flow. */
    fun getResponsesFlow(invitationId: String): Flow<List<InvitationResponse>>

    /** Gets the count of confirmed guests for an invitation. */
    suspend fun getConfirmedCount(invitationId: String): Int

    /** Adds a guest response (RSVP). */
    suspend fun addResponse(response: InvitationResponse): Result<InvitationResponse>

    /** Updates a guest response. */
    suspend fun updateResponse(response: InvitationResponse): Result<InvitationResponse>
}
