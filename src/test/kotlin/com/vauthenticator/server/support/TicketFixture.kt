package com.vauthenticator.server.support

import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.VerificationTicket

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), mail, clientAppId, 200)
}