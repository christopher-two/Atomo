package org.override.atomo.domain.usecase.invitation

import kotlinx.coroutines.flow.Flow
import org.override.atomo.domain.model.Invitation
import org.override.atomo.domain.model.InvitationResponse
import org.override.atomo.domain.repository.InvitationRepository

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

class GetInvitationsUseCase(private val repository: InvitationRepository) {
    operator fun invoke(userId: String): Flow<List<Invitation>> = repository.getInvitationsFlow(userId)
}

class GetInvitationUseCase(private val repository: InvitationRepository) {
    operator fun invoke(invitationId: String): Flow<Invitation?> = repository.getInvitationFlow(invitationId)
}

class SyncInvitationsUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(userId: String): Result<List<Invitation>> = repository.syncInvitations(userId)
}

class CreateInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitation: Invitation): Result<Invitation> = repository.createInvitation(invitation)
}

class UpdateInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitation: Invitation): Result<Invitation> = repository.updateInvitation(invitation)
}

class DeleteInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitationId: String): Result<Unit> = repository.deleteInvitation(invitationId)
}

class AddResponseUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(response: InvitationResponse): Result<InvitationResponse> = repository.addResponse(response)
}

class GetConfirmedCountUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitationId: String): Int = repository.getConfirmedCount(invitationId)
}
