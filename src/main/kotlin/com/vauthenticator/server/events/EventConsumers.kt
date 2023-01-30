package com.vauthenticator.server.events

import org.slf4j.LoggerFactory

class LoggerEventConsumer : EventConsumer {

    private val logger = LoggerFactory.getLogger(LoggerEventConsumer::class.java)

    override fun accept(event: VAuthenticatorEvent) {
        val logLine = """
            The user : ${event.clientAppId.content} 
            with the client id ${event.userName.content} 
            has done ${event.payload::class.simpleName} event
            event at ${event.timeStamp.epochSecond}
            event payload: ${event.payload}
     """.trimIndent()
        logger.info(logLine)
    }
}
