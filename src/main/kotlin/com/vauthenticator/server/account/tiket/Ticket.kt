package com.vauthenticator.server.account.tiket

import java.time.Duration

data class Ticket(val verificationTicket: VerificationTicket,
                  val features: VerificationTicketFeatures,
                  val email: String,
                  val clientAppId: String
)
data class VerificationTicket(val content: String)

data class VerificationTicketFeatures(val ttl: Duration)

class InvalidTicketException(message: String) : RuntimeException(message)