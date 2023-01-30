package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.mfa.MfaAuthentication
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import io.mockk.mockk
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import java.time.Instant

object EventFixture {

//    val defaultSpringEvent = DefaultSpringEvent(Email("anemail@domain.com"), ClientAppId("A_CLIENT_APP_ID"), Instant.now(), AuthenticationSuccessEvent(mockk()))
//    val mfaEvent = VAuthenticatorMFAEvent(Email("anemail@domain.com"), ClientAppId("A_CLIENT_APP_ID"), Instant.now(), MfaAuthentication(mockk()))
}