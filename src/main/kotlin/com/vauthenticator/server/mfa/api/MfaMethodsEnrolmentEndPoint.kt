package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class MfaEnrolmentAssociationEndPoint(
    private val sensitiveEmailMasker: SensitiveEmailMasker,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val mfaMethodsEnrollment: MfaMethodsEnrollment,
    private val accountRepository: AccountRepository,
    private val mfaMethodsEnrolmentAssociation: MfaMethodsEnrollmentAssociation
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


    @PostMapping("/api/mfa/enrollment")
    fun enrollMfa(authentication: Authentication, enrolling: MfaEnrollingDevice) {
        accountRepository.accountFor(authentication.name)
            .map { account ->
                when (enrolling) {
                    is EmailMfaDevice -> mfaMethodsEnrollment.enroll(
                        account,
                        enrolling.mfaMethod,
                        enrolling.email,
                        ClientAppId.empty(),
                        true
                    )

                    else -> {}
                }
            }
    }

    @PostMapping("/api/mfa/associate")
    fun associateMfaEnrollment(authentication: Authentication, @RequestParam ticket: String) {
        mfaMethodsEnrolmentAssociation.associate(ticket)
    }

    @DeleteMapping("/api/mfa/enrollment/{enrollmentId}")
    fun deleteMfaAssociation(
        @PathVariable("enrollmentId") enrollmentId: String,
        authentication: Authentication
    ) {

    }
}