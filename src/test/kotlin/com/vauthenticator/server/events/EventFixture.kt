package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import java.time.Instant

object EventFixture {

    val userLoggedEvent = UserLogged(Email("anemail@domain.com"), ClientAppId("A_CLIENT_APP_ID"), Instant.now())
}