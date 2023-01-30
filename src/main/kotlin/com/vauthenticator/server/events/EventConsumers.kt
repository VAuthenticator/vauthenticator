package com.vauthenticator.server.events

import org.slf4j.LoggerFactory

class LoggerEventConsumer : EventConsumer {

    private val logger = LoggerFactory.getLogger(LoggerEventConsumer::class.java)

    override fun accept(event: VAuthenticatorEvent) {
        val logLine = """
            The user ${event.userName.content}
            with the client id ${event.clientAppId.content}
            has done an ${event.payload::class.simpleName} event
            event at ${event.timeStamp.epochSecond}
            event payload: ${event.payload}
     """.trimIndent()
        logger.info(logLine)
    }
}
