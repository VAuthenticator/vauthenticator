package com.vauthenticator.server.mfa.domain

import java.time.Duration

data class Ticket(val verificationTicket: VerificationTicket,
                  val email: String,
                  val clientAppId: String,
                  val ttl : Long
)
data class VerificationTicket(val content: String)

data class VerificationTicketFeatures(val ttl: Duration)

class InvalidTicketException(message: String) : RuntimeException(message)