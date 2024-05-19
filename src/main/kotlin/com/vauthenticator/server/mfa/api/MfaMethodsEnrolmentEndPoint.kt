package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MfaEnrolmentAssociationEndPoint(
    private val sensitiveEmailMasker: SensitiveEmailMasker,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository
) {


    @GetMapping("/api/mfa/enrollment")
    fun findAllAssociatedEnrolledMfaMethods(authentication: Authentication) =
        ok(
            mfaAccountMethodsRepository.findAll(authentication.name)
                .map {
                    when (it.method) {
                        MfaMethod.EMAIL_MFA_METHOD -> EmailMfaEnrolledDeviceResponse(sensitiveEmailMasker.mask(it.email), it.method.name)
                        MfaMethod.SMS_MFA_METHOD -> TODO()
                        MfaMethod.OTP_MFA_METHOD -> TODO()
                    }

                }
        )

}

sealed class MfaEnrolledDeviceResponse(val mfaMethod: String)
class EmailMfaEnrolledDeviceResponse(val email: String, mfaMethod: String) : MfaEnrolledDeviceResponse(mfaMethod)

