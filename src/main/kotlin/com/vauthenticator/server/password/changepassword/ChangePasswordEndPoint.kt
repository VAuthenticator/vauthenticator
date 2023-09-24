package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.password.PasswordPolicyViolation
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.internalServerError
import org.springframework.http.ResponseEntity.noContent
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.ExceptionHandler
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

    @ExceptionHandler(AccountNotFoundException::class, PasswordPolicyViolation::class)
    fun exceptionHandler(): ResponseEntity<Unit> {
        return internalServerError().build()
    }
}

