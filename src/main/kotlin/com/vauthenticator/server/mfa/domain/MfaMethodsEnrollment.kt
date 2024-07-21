package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.*

class MfaMethodsEnrollmentAssociation(
    private val ticketRepository: TicketRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    //todo mfaMethod: MfaMethod can be encoded in the ticket itself
    //todo ticket can be an higher abstraction like RawTicket
    fun associate(ticket: String, mfaMethod: MfaMethod) {
        ticketRepository.loadFor(TicketId(ticket))
            .map { ticket ->
                val email = ticket.userName
                val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
                if (!mfaAccountMethods.any { it.method == mfaMethod }) {
                    mfaAccountMethodsRepository.save(email, mfaMethod)
                }

                revoke(ticket)
                ticket.userName
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket, it seems to be expired") }
    }

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.ticketId)
}

class MfaMethodsEnrollment(
    private val ticketCreator: TicketCreator,
    private val mfaSender: OtpMfaSender,
) {

    fun enroll(
        account: Account,
        emailMfaMethod: MfaMethod,
        mfaChannel: String,
        clientAppId: ClientAppId,
        sendChallengeCode: Boolean = true
    ): TicketId {
        if (sendChallengeCode) {
            mfaSender.sendMfaChallenge(account.email)
        }
        return ticketCreator.createTicketFor(account, clientAppId)
    }
}