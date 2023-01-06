package com.vauthenticator.account.tiket

import com.vauthenticator.account.Account
import com.vauthenticator.oauth2.clientapp.ClientAppId
import com.vauthenticator.time.Clocker
import java.time.Duration


class VerificationTicketFactory(private val ticketGenerator: () -> String,
                                private val clocker: Clocker,
                                private val ticketRepository: TicketRepository,
                                private val verificationTicketFeatures: VerificationTicketFeatures
) {
    fun createTicketFor(account: Account, clientAppId: ClientAppId): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
                verificationTicket,
                verificationTicketFeatures.copy(ttl = verificationTicketFeatures.ttl.plus(Duration.ofSeconds(clocker.now().epochSecond))),
                account.email,
                clientAppId.content
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}