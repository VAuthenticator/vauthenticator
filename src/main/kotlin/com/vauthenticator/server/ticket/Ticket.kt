package com.vauthenticator.server.ticket

import java.time.Duration

data class Ticket(
    val ticketId: TicketId,
    val userName: String,
    val clientAppId: String,
    val ttl: Long,
    val context: TicketContext = TicketContext.empty(),
) {
    companion object {
        const val MFA_CHANNEL_CONTEXT_KEY = "mfaChannel"
        const val MFA_METHOD_CONTEXT_KEY = "mfaMethod"
        const val MFA_AUTO_ASSOCIATION_CONTEXT_KEY = "auto-association"
        const val MFA_AUTO_ASSOCIATION_CONTEXT_VALUE = "true"
    }
}

data class TicketContext(val content: Map<String, String>) {

    companion object {
        fun empty() = TicketContext(emptyMap())
    }
}

data class TicketId(val content: String)

data class TicketFeatures(val ttl: Duration)

class InvalidTicketException(message: String) : RuntimeException(message)