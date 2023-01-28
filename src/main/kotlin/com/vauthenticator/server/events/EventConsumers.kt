package com.vauthenticator.server.events

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory

class LoggerEventConsumer : EventConsumer {

    private val logger = LoggerFactory.getLogger(LoggerEventConsumer::class.java)
    override fun accept(event: VAuthenticatorEvent) {
        logger.info("The user : ${event.clientAppId.content} with the client id ${event.email.content} has done a ${event::class.simpleName} event at ${event.timeStamp.epochSecond}")
    }
}

class PrometheusEventConsumer(private val registry: MeterRegistry) : EventConsumer {

    override fun accept(event: VAuthenticatorEvent) {
        Counter
            .builder(event::class.simpleName!!)
            .tag("user_name", event.email.content)
            .tag("client_id", event.clientAppId.content)
            .tag("time_stamp", event.timeStamp.epochSecond.toString())
            .register(registry)
    }
}