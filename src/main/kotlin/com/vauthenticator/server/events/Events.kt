package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
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
    val email: Email,
    val clientAppId: ClientAppId,
    val timeStamp: Instant
)

class Authorized(email: Email, clientAppId: ClientAppId, timeStamp: Instant) :
    VAuthenticatorEvent(email, clientAppId, timeStamp)

class Authorizing(email: Email, clientAppId: ClientAppId, timeStamp: Instant) :
    VAuthenticatorEvent(email, clientAppId, timeStamp)

class UserLogged(email: Email, clientAppId: ClientAppId, timeStamp: Instant) :
    VAuthenticatorEvent(email, clientAppId, timeStamp)

class UserLoginError(email: Email, clientAppId: ClientAppId, timeStamp: Instant) :
    VAuthenticatorEvent(email, clientAppId, timeStamp)