package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mask.SensitiveEmailMasker
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
                        MfaMethod.EMAIL_MFA_METHOD -> EmailMfaEnrolledDevice(
                            sensitiveEmailMasker.mask(it.email),
                            it.method.name
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
    @PutMapping("/api/mfa/enrollment")
    fun enrollMfa(authentication: Authentication, enrolling: MfaEnrolledDevice) {
        TODO("will return enrollment_id")
    }

    @PostMapping("/api/mfa/associate")
    fun associateMfaEnrollment(authentication: Authentication) {

    }

    @DeleteMapping("/api/mfa/enrollment/{enrollmentId}/associate")
    fun deleteMfaAssociation(
        @PathVariable("enrollmentId") enrollmentId: String,
        authentication: Authentication
    ) {

    }
}

data class MfaEnrollmentDeviceResponse(val enrollmentId: String)

sealed class MfaEnrolledDevice(val mfaMethod: String)
class EmailMfaEnrolledDevice(val email: String, mfaMethod: String) : MfaEnrolledDevice(mfaMethod)

