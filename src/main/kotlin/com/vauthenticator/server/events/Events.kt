package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.boot.context.properties.ConfigurationProperties
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
    fun handleable(): Boolean

}

@ConfigurationProperties("event.consumer")
data class EventConsumerConfig(val enable: Map<String, Boolean>)

sealed class VAuthenticatorEvent(
    val userName: Email,
    val clientAppId: ClientAppId,
    val timeStamp: Instant,
    val payload: Any
)


class VAuthenticatorAuthEvent(
    userName: Email,
    clientAppId: ClientAppId,
    timeStamp: Instant,
    payload: AbstractAuthenticationEvent
) : VAuthenticatorEvent(userName, clientAppId, timeStamp, payload) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
