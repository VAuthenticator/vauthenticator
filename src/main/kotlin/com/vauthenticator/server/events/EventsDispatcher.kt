package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.extentions.oauth2ClientId
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant
import java.util.*

class VAuthenticatorEventsDispatcher(private val publisher: ApplicationEventPublisher) : EventsDispatcher {
    override fun dispatch(event: VAuthenticatorEvent) {
        publisher.publishEvent(event)
    }

}

class DefaultSpringEventEventsDispatcher(private val publisher: ApplicationEventPublisher) : EventsDispatcher {

    private val logger = LoggerFactory.getLogger(DefaultSpringEventEventsDispatcher::class.java)

    @EventListener
    fun handle(event: AbstractAuthenticationEvent) {
        val currentRequest: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        logger.info("PRE EVENT PROCESSING")
        clientIdForm(currentRequest)
            .ifPresentOrElse({
                logger.info("EVENT PROCESSING")

                dispatch(
                    DefaultSpringEvent(
                        Email((Optional.ofNullable(currentRequest.remoteUser)).orElseGet { "UNKNOWN" }),
                        ClientAppId(it),
                        Instant.now(),
                        event
                    )
                )
            },
                {
                    logger.info("PRE EVENT NOT PROCESSED")
                }
            )

    }

    private fun clientIdForm(currentRequest: HttpServletRequest): Optional<String> =
        currentRequest.oauth2ClientId().or { currentRequest.session.oauth2ClientId() }

    override fun dispatch(event: VAuthenticatorEvent) {
        publisher.publishEvent(event)
    }
}
