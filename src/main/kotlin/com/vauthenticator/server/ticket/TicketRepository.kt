package com.vauthenticator.server.ticket

import java.util.*

interface TicketRepository {
    fun store(ticket: Ticket)
    fun loadFor(verificationTicket: VerificationTicket): Optional<Ticket>
    fun delete(verificationTicket: VerificationTicket)
}

