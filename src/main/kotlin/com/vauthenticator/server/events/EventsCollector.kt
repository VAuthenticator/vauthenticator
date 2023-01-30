package com.vauthenticator.server.events

import org.springframework.context.event.EventListener

class SpringEventsCollector(private val eventConsumers: List<EventConsumer>) : EventsCollector {

    @EventListener
    override fun accept(event: VAuthenticatorEvent) {
        eventConsumers.forEach { it.accept(event) }
    }
}