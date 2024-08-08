package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.ticket.TicketContext
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketId
import org.slf4j.LoggerFactory

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
                mfaAccountMethodsRepository.findBy(userName, mfaMethod, mfaChannel)
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