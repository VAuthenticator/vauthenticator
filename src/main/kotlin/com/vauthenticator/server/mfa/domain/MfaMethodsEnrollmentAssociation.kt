package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.ticket.InvalidTicketException
import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketId
import com.vauthenticator.server.ticket.TicketRepository

typealias MfaAssociationVerifier = (ticket: Ticket) -> Unit

class MfaMethodsEnrollmentAssociation(
    private val ticketRepository: TicketRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val otpMfaVerifier: OtpMfaVerifier
) {

    fun associate(ticketId: String) {
        associate(
            ticketId,
        ) {
            if (it.context.isMfaNotSelfAssociable()) {
                throw InvalidTicketException("Mfa association without code is allowed only if in the ticket context there is ${Ticket.MFA_SELF_ASSOCIATION_CONTEXT_KEY} feature enabled")
            }
        }

    }

    fun associate(ticketId: String, code: String) {
        associate(
            ticketId,
        ) {
            otpMfaVerifier.verifyMfaChallengeToBeAssociatedFor(
                it.userName,
                it.context.mfaMethod(),
                it.context.mfaChannel(),
                MfaChallenge(code)
            )
        }
    }

    private fun associate(ticket: String, verifier: MfaAssociationVerifier) {
        ticketRepository.loadFor(TicketId(ticket))
            .map { ticket ->
                verifier.invoke(ticket)
                mfaAccountMethodsRepository.save(
                    ticket.userName,
                    ticket.context.mfaMethod(),
                    ticket.context.mfaChannel(),
                    true
                )
                revoke(ticket)
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket, it seems to be expired") }
    }

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.ticketId)
}