package com.vauthenticator.server.support

import com.vauthenticator.server.account.ticket.Ticket
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.account.ticket.VerificationTicketFeatures
import java.time.Duration

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), VerificationTicketFeatures(Duration.ofSeconds(200)), mail, clientAppId)
}