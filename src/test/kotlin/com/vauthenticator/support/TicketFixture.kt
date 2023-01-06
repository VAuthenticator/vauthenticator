package com.vauthenticator.support

import com.vauthenticator.account.tiket.Ticket
import com.vauthenticator.account.tiket.VerificationTicket
import com.vauthenticator.account.tiket.VerificationTicketFeatures
import java.time.Duration

object TicketFixture {
    fun ticketFor(verificationTicketValue: String, mail: String, clientAppId: String) =
            Ticket(VerificationTicket(verificationTicketValue), VerificationTicketFeatures(Duration.ofSeconds(200)), mail, clientAppId)
}