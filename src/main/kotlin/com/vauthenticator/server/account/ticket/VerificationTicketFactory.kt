package com.vauthenticator.server.account.ticket

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import java.time.Clock
import java.time.Duration
import java.time.Instant


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
            verificationTicketFeatures.copy(ttl = verificationTicketFeatures.ttl.plus(Duration.ofSeconds(Instant.now(clock).epochSecond))),
            account.email,
            clientAppId.content
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}