package com.vauthenticator.server.ticket

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.extentions.expirationTimeStampInSecondFromNow
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import java.time.Clock

/*
* This domain class create a new verification ticket storing the associated information in the database
* */
class VerificationTicketFactory(
    private val ticketGenerator: () -> String,
    private val clock: Clock,
    private val ticketRepository: TicketRepository,
    private val verificationTicketFeatures: VerificationTicketFeatures
) {
    fun createTicketFor(account: Account, clientAppId: ClientAppId): VerificationTicket {
        val verificationTicket = VerificationTicket(ticketGenerator.invoke())
        val ticket = Ticket(
            verificationTicket,
            account.email,
            clientAppId.content,
            verificationTicketFeatures.ttl.expirationTimeStampInSecondFromNow(clock)
        )
        ticketRepository.store(ticket)
        return verificationTicket
    }

}