package com.vauthenticator.server.events

class SpringEventsCollector(private val eventConsumers: List<EventConsumer>) : EventsCollector {
    override fun accept(event: VAuthenticatorEvent) {
        eventConsumers.forEach { it.accept(event) }
    }
}