package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import it.valeriovaudi.vauthenticator.time.Clock
import org.springframework.security.oauth2.provider.OAuth2Authentication

data class IdToken(val userName: String,
                   val iss: String,
                   val sub: String,
                   val aud: String,
                   val exp: Long,
                   val iat: Long,
                   val auth_time: Long) {
    companion object {
        fun createIdToken(iss: String,
                          sub: String,
                          authentication: OAuth2Authentication,
                          clock: Clock) =
                IdToken(authentication.name,
                        iss, sub, authentication.oAuth2Request.clientId,
                        clock.nowInSeconds() * 20,
                        clock.nowInSeconds(),
                        clock.nowInSeconds())
    }
}