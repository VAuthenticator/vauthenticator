package it.valeriovaudi.vauthenticator.support

import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import java.time.Duration

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), VerificationTicketFeatures(Duration.ofSeconds(100), false), mail, clientAppId)
}