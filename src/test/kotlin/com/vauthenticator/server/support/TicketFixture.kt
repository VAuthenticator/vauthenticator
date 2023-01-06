package com.vauthenticator.server.support

import com.vauthenticator.server.account.tiket.Ticket
import com.vauthenticator.server.account.tiket.VerificationTicket
import com.vauthenticator.server.account.tiket.VerificationTicketFeatures
import java.time.Duration

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), VerificationTicketFeatures(Duration.ofSeconds(200)), mail, clientAppId)
}