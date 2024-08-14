package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mfa.domain.MfaAccountMethodsRepository
import com.vauthenticator.server.mfa.domain.MfaDeviceId
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.OtpMfaSender
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MfaChallengeEndPoint(
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val otpMfaSender: OtpMfaSender
) {

    @PutMapping("/api/mfa/challenge")
    fun sendMfaChallenge(
        authentication: Authentication,
        @RequestParam("mfa-device-id", required = false) mfaDeviceId: Optional<String>,
    ) {
        //todo it should be an usecase
        mfaDeviceId.ifPresentOrElse(
            {
                //todo it should be improved checking if the mfa account method returned belong to the actual requesting user
                mfaAccountMethodsRepository.findBy(MfaDeviceId(it))
                    .map {
                        otpMfaSender.sendMfaChallengeFor(
                            authentication.name,
                            MfaMethod.EMAIL_MFA_METHOD,
                            it.mfaChannel
                        )
                    }

            },
            {
                mfaAccountMethodsRepository.getDefaultDevice(authentication.name)
                    .flatMap { mfaAccountMethodsRepository.findBy(it) }
                    .map {
                        otpMfaSender.sendMfaChallengeFor(
                            authentication.name,
                            MfaMethod.EMAIL_MFA_METHOD,
                            it.mfaChannel
                        )
                    }

            }
        )
    }

}