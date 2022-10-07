package it.valeriovaudi.vauthenticator.extentions

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun JwtAuthenticationToken.clientAppId(): ClientAppId =
        when (this.token.claims["aud"]!!) {
            String::class -> ClientAppId((this.token.claims["aud"]!! as String))
            else -> ClientAppId((this.token.claims["aud"]!! as List<String>)[0])
        }