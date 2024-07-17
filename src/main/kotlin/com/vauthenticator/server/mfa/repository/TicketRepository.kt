package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.mfa.domain.Ticket
import com.vauthenticator.server.mfa.domain.VerificationTicket
import java.util.*

interface TicketRepository {
    fun store(ticket: Ticket)
    fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket>
    fun delete(verificationTicket: VerificationTicket)
}

