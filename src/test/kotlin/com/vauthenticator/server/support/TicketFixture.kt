package com.vauthenticator.server.support

import com.vauthenticator.server.mfa.domain.Ticket
import com.vauthenticator.server.mfa.domain.VerificationTicket

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), mail, clientAppId, 200)
}