package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.EmailMfaDevice
import com.vauthenticator.server.mfa.domain.MfaEnrollingDevice
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

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
                        MfaMethod.EMAIL_MFA_METHOD -> EmailMfaDevice(
                            sensitiveEmailMasker.mask(it.email),
                            it.method
                        )

                        MfaMethod.SMS_MFA_METHOD -> TODO()
                        MfaMethod.OTP_MFA_METHOD -> TODO()
                    }

                }
        )


    /*
    * /api/mfa/enrollment -> enrollmentId
    *
    * */
    @PostMapping("/api/mfa/enrollment")
    fun enrollMfa(authentication: Authentication, enrolling: MfaEnrollingDevice) {
        TODO("will return ticket to enroll")
    }

    @PostMapping("/api/mfa/associate")
    fun associateMfaEnrollment(authentication: Authentication) {

    }

    @DeleteMapping("/api/mfa/enrollment/{enrollmentId}")
    fun deleteMfaAssociation(
        @PathVariable("enrollmentId") enrollmentId: String,
        authentication: Authentication
    ) {

    }
}

data class MfaEnrollmentDeviceResponse(val enrollmentId: String)


