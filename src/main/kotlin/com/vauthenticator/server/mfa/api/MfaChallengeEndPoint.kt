package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.OtpMfaSender
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MfaChallengeEndPoint(private val otpMfaSender: OtpMfaSender) {

    @PutMapping("/api/mfa/challenge")

    fun sendMfaChallenge(

        authentication: Authentication,
        @RequestParam("mfa-device-id", required = false) mfaDeviceId: Optional<String>,
    ) {
        otpMfaSender.sendMfaChallenge(authentication.name, MfaMethod.EMAIL_MFA_METHOD, authentication.name)
    }

}