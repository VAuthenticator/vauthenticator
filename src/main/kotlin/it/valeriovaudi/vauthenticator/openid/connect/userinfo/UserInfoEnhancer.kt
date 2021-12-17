package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class UserInfoEnhancer {

    private fun authorities(principal: JwtAuthenticationToken) =
            principal.token.claims["authorities"] as List<String>

    private fun userName(principal: JwtAuthenticationToken) =
            principal.token.claims["user_name"] as String
}