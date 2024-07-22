package com.vauthenticator.server.support

import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketContext
import com.vauthenticator.server.ticket.TicketId

object TicketFixture {

    fun ticketContext(email: String) = TicketContext(
        mapOf(
            "mfaChannel" to email,
            "mfaMethod" to MfaMethod.EMAIL_MFA_METHOD.name
        )
    )

    fun ticketFor(verificationTicketValue: String, email: String, clientAppId: String): Ticket {

        return Ticket(
            TicketId(verificationTicketValue), email, clientAppId, 200,
            ticketContext(email)
        )
    }
}