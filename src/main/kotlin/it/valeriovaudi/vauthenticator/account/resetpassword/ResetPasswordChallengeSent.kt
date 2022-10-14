package it.valeriovaudi.vauthenticator.account.resetpassword

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.InvalidTicketException
import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import java.util.*

class ResetPasswordChallengeSent(private val accountRepository: AccountRepository,
                                 private val ticketRepository: TicketRepository) {
    fun resetPassword(verificationTicket: VerificationTicket, request: ResetPasswordRequest) {
        invalidateTicketFor(verificationTicket)
                .map { passwordResetFor(it, request) }
                .orElseThrow { throw InvalidTicketException("Te ticket ${verificationTicket.content} is not a valid ticket it seems to be used or expired") }
    }

    private fun invalidateTicketFor(verificationTicket: VerificationTicket): Optional<Ticket> =
            ticketRepository.loadFor(verificationTicket).map { ticket ->
                ticketRepository.delete(verificationTicket)
                ticket
            }

    private fun passwordResetFor(it: Ticket, request: ResetPasswordRequest): Optional<Unit> =
            accountRepository.accountFor(it.email).map {
                val newAccount = it.copy(password = request.newPassword)
                accountRepository.save(newAccount)
            }

}