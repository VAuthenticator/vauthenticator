package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.mfa.repository.TicketRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId

class MfaMethodsEnrollmentAssociation(
    private val ticketRepository: TicketRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    //todo mfaMethod: MfaMethod can be encoded in the ticket itself
    //todo ticket can be an higher abstraction like RawTicket
    fun associate(ticket: String, mfaMethod: MfaMethod) {
        ticketRepository.loadFor(VerificationTicket(ticket))
            .map { ticket ->
                val email = ticket.email
                val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
                if (!mfaAccountMethods.any { it.method == mfaMethod }) {
                    mfaAccountMethodsRepository.save(email, mfaMethod)
                }

                revoke(ticket)
                ticket.email
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket, it seems to be expired") }
    }

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.verificationTicket)
}

class MfaMethodsEnrollment(
    private val verificationTicketFactory: VerificationTicketFactory,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    fun enroll(
        account: Account,
        emailMfaMethod: MfaMethod,
        clientAppId: ClientAppId,
        sendChallengeCode : Boolean = true
    ): VerificationTicket {
        return verificationTicketFactory.createTicketFor(account, clientAppId)
    }
}