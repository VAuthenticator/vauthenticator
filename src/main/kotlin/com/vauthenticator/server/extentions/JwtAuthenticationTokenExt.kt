package com.vauthenticator.server.extentions

import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun JwtAuthenticationToken.clientAppId(): ClientAppId {
    val aud = this.token.claims["aud"]!!
    return try {
        ClientAppId((aud as String))
    } catch (e : RuntimeException){
        ClientAppId((aud as List<String>)[0])
    }
}
