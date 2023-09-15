package com.vauthenticator.server.account.changepassword

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChangePasswordEndPoint(val changePassword: ChangePassword) {

    @PutMapping("/api/password")
    fun sendVerifyMail(
        @RequestBody request: Map<String, String>,
        principal: JwtAuthenticationToken
    ): ResponseEntity<Unit> {
        changePassword.resetPasswordFor(principal, ChangePasswordRequest(request["pwd"]!!))
        return noContent().build()
    }

}

