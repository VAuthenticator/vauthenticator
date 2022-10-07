package it.valeriovaudi.vauthenticator.account.mailverification

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication


class MailVerificationTicketFactory(private val ticketGenerator: () -> String,
                                    private val ticketRepository: TicketRepository,
                                    private val verificationTicketFeatures: VerificationTicketFeatures) {
    fun createTicketFor(account: Account, clientApplication: ClientApplication): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
                verificationTicket,
                verificationTicketFeatures,
                account.email,
                clientApplication.clientAppId.content
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}