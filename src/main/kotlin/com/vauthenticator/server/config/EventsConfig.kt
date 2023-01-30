package com.vauthenticator.server.config

import com.vauthenticator.server.events.LoggerEventConsumer
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.events.SpringEventEventsDispatcher
import com.vauthenticator.server.events.SpringEventsCollector
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class EventsConfig {

    @Bean
    fun eventsDispatcherAdapter(publisher: ApplicationEventPublisher) =
        SpringEventEventsDispatcher(publisher)

    @Bean
    fun eventsDispatcher(publisher: ApplicationEventPublisher) =
        VAuthenticatorEventsDispatcher(publisher)

    @Bean
    fun eventsCollector() =
        SpringEventsCollector(
            listOf(LoggerEventConsumer())
        )

}