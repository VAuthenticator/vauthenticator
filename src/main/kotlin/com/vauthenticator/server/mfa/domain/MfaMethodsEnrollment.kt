package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.ticket.*
import com.vauthenticator.server.ticket.Ticket.Companion.MFA_SELF_ASSOCIATION_CONTEXT_KEY
import org.slf4j.LoggerFactory

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
    private val accountRepository: AccountRepository,
    private val ticketCreator: TicketCreator,
    private val mfaSender: OtpMfaSender,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {

    private val logger = LoggerFactory.getLogger(MfaMethodsEnrollment::class.java)

    fun enroll(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        clientAppId: ClientAppId,
        sendChallengeCode: Boolean = true,
        ticketContextAdditionalProperties: Map<String, String> = emptyMap()
    ): TicketId {
        return accountRepository.accountFor(userName)
            .map {
                mfaAccountMethodsRepository.findOne(userName, mfaMethod, mfaChannel)
                    .ifPresentOrElse({},
                        { mfaAccountMethodsRepository.save(userName, mfaMethod, mfaChannel, false) }
                    )

                if (sendChallengeCode) {
                    mfaSender.sendMfaChallenge(userName, mfaMethod, mfaChannel)
                }

                ticketCreator.createTicketFor(
                    it,
                    clientAppId,
                    TicketContext.mfaContextFor(
                        mfaMethod = mfaMethod,
                        mfaChannel = mfaChannel,
                        ticketContextAdditionalProperties = ticketContextAdditionalProperties
                    )
                )
            }.orElseThrow {
                logger.warn("account not found")
                AccountNotFoundException("account not found")
            }
    }
}