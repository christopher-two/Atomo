/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.usecase.invitation

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse
import org.override.atomo.domain.repository.InvitationRepository

/**
 * Wrapper for all Invitation-related use cases.
 *
 * @property getInvitations Retrieves all Invitations for a user.
 * @property getInvitation Retrieves a single Invitation by ID.
 * @property syncInvitations Synchronizes Invitations from the backend.
 * @property createInvitation Creates a new Invitation.
 * @property updateInvitation Updates an existing Invitation.
 * @property deleteInvitation Deletes an Invitation.
 * @property addResponse Adds a guest response (RSVP).
 * @property getConfirmedCount Gets the count of confirmed guests.
 */
data class InvitationUseCases(
    val getInvitations: GetInvitationsUseCase,
    val getInvitation: GetInvitationUseCase,
    val syncInvitations: SyncInvitationsUseCase,
    val createInvitation: CreateInvitationUseCase,
    val updateInvitation: UpdateInvitationUseCase,
    val deleteInvitation: DeleteInvitationUseCase,
    val addResponse: AddResponseUseCase,
    val getConfirmedCount: GetConfirmedCountUseCase
)

/** Retrieves all invitations for a user as a Flow. */
class GetInvitationsUseCase(private val repository: InvitationRepository) {
    operator fun invoke(userId: String): Flow<List<Invitation>> = repository.getInvitationsFlow(userId)
}

/** Retrieves a single invitation by ID as a Flow. */
class GetInvitationUseCase(private val repository: InvitationRepository) {
    operator fun invoke(invitationId: String): Flow<Invitation?> = repository.getInvitationFlow(invitationId)
}

/** Synchronizes invitations from the server. */
class SyncInvitationsUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(userId: String): Result<List<Invitation>> = repository.syncInvitations(userId)
}

/** Creates a new invitation. */
class CreateInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitation: Invitation): Result<Invitation> = repository.createInvitation(invitation)
}

/** Updates an existing invitation. */
class UpdateInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitation: Invitation): Result<Invitation> = repository.updateInvitation(invitation)
}

/** Deletes an invitation by ID. */
class DeleteInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitationId: String): Result<Unit> = repository.deleteInvitation(invitationId)
}

/** Adds a guest response (RSVP) to an invitation. */
class AddResponseUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(response: InvitationResponse): Result<InvitationResponse> = repository.addResponse(response)
}

/** Gets the count of confirmed guests for an invitation. */
class GetConfirmedCountUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitationId: String): Int = repository.getConfirmedCount(invitationId)
}
