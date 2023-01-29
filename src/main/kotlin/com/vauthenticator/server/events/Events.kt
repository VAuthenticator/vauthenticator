package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import java.time.Instant

interface EventsDispatcher {
    fun dispatch(event: VAuthenticatorEvent)
}

interface EventsCollector {
    fun accept(event: VAuthenticatorEvent)

}

interface EventConsumer {
    fun accept(event: VAuthenticatorEvent)

}

sealed class VAuthenticatorEvent(
    val userName: Email,
    val clientAppId: ClientAppId,
    val timeStamp: Instant
)

class DefaultSpringEvent(
    email: Email,
    clientAppId: ClientAppId,
    timeStamp: Instant,
    val source: AbstractAuthenticationEvent
) : VAuthenticatorEvent(email, clientAppId, timeStamp)

class VAuthenticatorMFAEvent(
    email: Email,
    clientAppId: ClientAppId,
    timeStamp: Instant
) : VAuthenticatorEvent(email, clientAppId, timeStamp)
