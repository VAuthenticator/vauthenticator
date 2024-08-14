package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mfa.domain.MfaDeviceId
import com.vauthenticator.server.mfa.domain.OtpMfaSender
import com.vauthenticator.server.oauth2.clientapp.domain.Scope
import com.vauthenticator.server.oauth2.clientapp.domain.Scopes
import com.vauthenticator.server.role.PermissionValidator
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MfaChallengeEndPoint(
    private val permissionValidator: PermissionValidator,
    private val otpMfaSender: OtpMfaSender
) {

    @PutMapping("/api/mfa/challenge")
    fun sendMfaChallenge(
        authentication: JwtAuthenticationToken,
        @RequestParam("mfa-device-id", required = false) mfaDeviceId: Optional<String>,
    ) {
        permissionValidator.validate(authentication, Scopes.from(Scope.MFA_ALWAYS))
        mfaDeviceId.ifPresentOrElse(
            { otpMfaSender.sendMfaChallengeFor(authentication.name, MfaDeviceId(it)) },
            { otpMfaSender.sendMfaChallengeFor(authentication.name) }
        )
    }

}