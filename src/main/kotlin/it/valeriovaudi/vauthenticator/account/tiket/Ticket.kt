package it.valeriovaudi.vauthenticator.account.tiket

import java.time.Duration

data class Ticket(val verificationTicket: VerificationTicket,
                  val features: VerificationTicketFeatures,
                  val email: String,
                  val clientAppId: String
)
data class VerificationTicket(val content: String)

data class VerificationTicketFeatures(val ttl: Duration, val fireAndForget: Boolean)

class InsufficientTicketException(message: String) : RuntimeException(message)