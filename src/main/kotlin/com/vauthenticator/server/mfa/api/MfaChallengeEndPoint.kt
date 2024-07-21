package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mfa.domain.OtpMfaSender
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MfaChallengeEndPoint(private val otpMfaSender: OtpMfaSender) {

    @PutMapping("/api/mfa/challenge")
    fun sendMfaChallenge(authentication: Authentication) {
        otpMfaSender.sendMfaChallenge(authentication.name, authentication.name)
    }

}