package com.vauthenticator.server.events

import org.springframework.context.event.EventListener

class SpringEventsCollector(private val eventConsumers: List<EventConsumer>) : EventsCollector {

    @EventListener
    override fun accept(event: VAuthenticatorEvent) {
        eventConsumers.forEach {
            println(it::class.java)
            println(it.handleable(event))
            if(it.handleable(event)){
                it.accept(event)
            }
        }
    }
}