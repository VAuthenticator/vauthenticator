package com.vauthenticator.server.events

import com.vauthenticator.server.extentions.decoder
import org.slf4j.LoggerFactory

class LoggerEventConsumer : EventConsumer {

    private val logger = LoggerFactory.getLogger(LoggerEventConsumer::class.java)

    override fun accept(event: VAuthenticatorEvent) {
        val logLine = """
            The user : ${event.clientAppId.content} 
            with the client id ${event.userName.content} 
            has done a ${eventClassFrom(event)} 
            event at ${event.timeStamp.epochSecond}
        """.trimIndent()

        logger.info(logLine)
    }

    private fun eventClassFrom(event: VAuthenticatorEvent) : String =
        when(event) {
             is DefaultSpringEvent -> event.source::class.simpleName!!
            else -> event::class.simpleName!!
        }
}
