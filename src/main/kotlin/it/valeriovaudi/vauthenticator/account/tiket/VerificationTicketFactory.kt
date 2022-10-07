package it.valeriovaudi.vauthenticator.account.tiket

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import it.valeriovaudi.vauthenticator.time.Clocker
import java.time.Duration


class VerificationTicketFactory(private val ticketGenerator: () -> String,
                                private val clocker: Clocker,
                                private val ticketRepository: TicketRepository,
                                private val verificationTicketFeatures: VerificationTicketFeatures) {
    fun createTicketFor(account: Account, clientApplication: ClientApplication): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
                verificationTicket,
                verificationTicketFeatures.copy(ttl = verificationTicketFeatures.ttl.plus(Duration.ofSeconds(clocker.now().epochSecond))),
                account.email,
                clientApplication.clientAppId.content
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}