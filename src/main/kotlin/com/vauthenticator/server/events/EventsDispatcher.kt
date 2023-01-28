package com.vauthenticator.server.events

import org.springframework.context.ApplicationEventPublisher

class SpringEventEventsDispatcher(private val publisher: ApplicationEventPublisher) : EventsDispatcher {
    override fun dispatch(event: VAuthenticatorEvent) {
        publisher.publishEvent(event)
    }

}
