package com.vauthenticator.server.support

import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketId

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(TicketId(verificationTicketValue), mail, clientAppId, 200)
}