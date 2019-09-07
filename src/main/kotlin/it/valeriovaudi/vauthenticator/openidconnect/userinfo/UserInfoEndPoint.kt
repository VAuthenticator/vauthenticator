package it.valeriovaudi.vauthenticator.openidconnect.userinfo

import org.springframework.http.ResponseEntity.ok
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoEndPoint(private val userInfoFactory: UserInfoFactory) {

    @GetMapping("/user-info")
    fun key(principal: JwtAuthenticationToken) =
            ok(userInfoFactory.newUserInfo(principal))


}