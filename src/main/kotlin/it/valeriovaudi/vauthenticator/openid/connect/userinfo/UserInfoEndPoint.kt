package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserInfoEndPoint(private val userInfoFactory: UserInfoFactory) {

    @GetMapping("/user-info")
    fun key(
        principal: JwtAuthenticationToken,
        @RequestHeader("Authorization", required = false) authorization: String?
    ): ResponseEntity<UserInfo> {
        println("authorization: $authorization")
        println("principal: $principal")
        return ok(userInfoFactory.newUserInfo(principal))
    }


}