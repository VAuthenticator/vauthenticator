package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.*
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_CHANNEL_CONTEXT_KEY
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_METHOD_CONTEXT_KEY

class MfaMethodsEnrollmentAssociation(
    private val ticketRepository: TicketRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    fun associate(ticket: String) {
        ticketRepository.loadFor(TicketId(ticket))
            .map { ticket ->
                val email = ticket.userName
                val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
                val mfaMethod = MfaMethod.valueOf(ticket.context.content[MFA_METHOD_CONTEXT_KEY]!!)
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
        mfaMethod: MfaMethod,
        mfaChannel: String,
        clientAppId: ClientAppId,
        sendChallengeCode: Boolean = true
    ): TicketId {
        if (sendChallengeCode) {
            mfaSender.sendMfaChallenge(account.email, mfaChannel)
        }
        return ticketCreator.createTicketFor(
            account,
            clientAppId,
            TicketContext(
                mapOf(
                    MFA_CHANNEL_CONTEXT_KEY to mfaChannel,
                    MFA_METHOD_CONTEXT_KEY to mfaMethod.name
                )
            )
        )
    }
}