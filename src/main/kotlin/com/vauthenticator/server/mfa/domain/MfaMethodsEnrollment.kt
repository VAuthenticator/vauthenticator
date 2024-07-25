package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.*
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_AUTO_ASSOCIATION_CONTEXT_KEY
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_AUTO_ASSOCIATION_CONTEXT_VALUE
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_CHANNEL_CONTEXT_KEY
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_METHOD_CONTEXT_KEY

class MfaMethodsEnrollmentAssociation(
    private val ticketRepository: TicketRepository,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val otpMfaVerifier: OtpMfaVerifier
) {

    fun associate(ticket: String) {
        associate(ticket) {
            it.context.content[MFA_AUTO_ASSOCIATION_CONTEXT_KEY] === MFA_AUTO_ASSOCIATION_CONTEXT_VALUE
        }

    }

    fun associate(ticket: String, code: String) {
        associate(ticket) { otpMfaVerifier.verifyMfaChallengeFor(it.userName, MfaChallenge(code)) }
    }

    private fun associate(ticket: String, verifier: (ticket: Ticket) -> Unit) {
        ticketRepository.loadFor(TicketId(ticket))
            .map { ticket ->
                verifier.invoke(ticket)
                revoke(ticket)
            }
            .orElseThrow { throw InvalidTicketException("The ticket $ticket is not a valid ticket, it seems to be expired") }
    }

    private fun revoke(ticket: Ticket) =
        ticketRepository.delete(ticket.ticketId)
}

class MfaMethodsEnrollment(
    private val ticketCreator: TicketCreator,
    private val mfaSender: OtpMfaSender,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    //TODO to be improved ..... better to take the user_name instead of the account itself
    fun enroll(
        account: Account,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        clientAppId: ClientAppId,
        sendChallengeCode: Boolean = true,
        ticketContextAdditionalProperties: Map<String, String> = emptyMap()
    ): TicketId {
        val email = account.email

        val mfaAccountMethods = mfaAccountMethodsRepository.findAll(email)
        if (!mfaAccountMethods.any { it.method == mfaMethod }) {
            mfaAccountMethodsRepository.save(email, mfaMethod, mfaChannel)
        }
        if (sendChallengeCode) {
            mfaSender.sendMfaChallenge(email, mfaChannel)
        }
        return ticketCreator.createTicketFor(
            account,
            clientAppId,
            TicketContext(
                mapOf(
                    MFA_CHANNEL_CONTEXT_KEY to mfaChannel,
                    MFA_METHOD_CONTEXT_KEY to mfaMethod.name
                ) + ticketContextAdditionalProperties
            )
        )
    }
}