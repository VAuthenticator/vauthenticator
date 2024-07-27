package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.*
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_SELF_ASSOCIATION_CONTEXT_KEY

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
                throw InvalidTicketException("Mfa association without code is allowed only if in the ticket context there is $MFA_SELF_ASSOCIATION_CONTEXT_KEY feature enabled")
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

        mfaAccountMethodsRepository.findOne(email, mfaMethod, mfaChannel)
            .ifPresentOrElse({},
                { mfaAccountMethodsRepository.save(email, mfaMethod, mfaChannel, false) }
            )

        if (sendChallengeCode) {
            mfaSender.sendMfaChallenge(email, mfaMethod, mfaChannel)
        }

        return ticketCreator.createTicketFor(
            account,
            clientAppId,
            TicketContext.mfaContextFor(
                mfaMethod = mfaMethod,
                mfaChannel = mfaChannel,
                ticketContextAdditionalProperties = ticketContextAdditionalProperties
            )
        )
    }
}