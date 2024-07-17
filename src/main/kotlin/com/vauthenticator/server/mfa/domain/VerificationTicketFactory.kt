package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.extentions.expirationTimeStampInSecondFromNow
import com.vauthenticator.server.mfa.repository.TicketRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import java.time.Clock


class VerificationTicketFactory(
    private val ticketGenerator: () -> String,
    private val clock: Clock,
    private val ticketRepository: TicketRepository,
    private val verificationTicketFeatures: VerificationTicketFeatures
) {
    fun createTicketFor(account: Account, clientAppId: ClientAppId): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
            verificationTicket,
            account.email,
            clientAppId.content,
            verificationTicketFeatures.ttl.expirationTimeStampInSecondFromNow(clock)
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}