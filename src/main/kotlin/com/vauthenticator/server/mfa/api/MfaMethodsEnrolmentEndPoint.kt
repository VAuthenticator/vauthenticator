package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.EmailMfaDevice
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollment
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.http.ResponseEntity
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
                            sensitiveEmailMasker.mask(it.userName),
                            it.method
                        )

                        MfaMethod.SMS_MFA_METHOD -> TODO()
                        MfaMethod.OTP_MFA_METHOD -> TODO()
                    }

                }
        )


    @PostMapping("/api/mfa/enrollment")
    fun enrollMfa(
        authentication: Authentication,
        @RequestBody enrolling: MfaEnrollmentRequest
    ): ResponseEntity<String> {
        // todo introduce validation on the expected fields 400 in case of error

        val ticketId = accountRepository.accountFor(authentication.name)
            .map { account ->
                mfaMethodsEnrollment.enroll(
                    account,
                    enrolling.mfaMethod,
                    enrolling.mfaChannel,
                    ClientAppId.empty(), //todo figure out how to detect the client app
                    true
                )
            }.orElseThrow()

        return ok(ticketId.content)
    }

    @PostMapping("/api/mfa/associate")
    fun associateMfaEnrollment(
        @RequestBody associationRequest: MfaEnrollmentAssociationRequest,
        authentication: Authentication
    ) {
        mfaMethodsEnrolmentAssociation.associate(associationRequest.ticket, associationRequest.code)
    }

    @DeleteMapping("/api/mfa/enrollment/{enrollmentId}")
    fun deleteMfaAssociation(
        @PathVariable("enrollmentId") enrollmentId: String,
        authentication: Authentication
    ) {

    }
}

data class MfaEnrollmentRequest(
    val mfaChannel: String,
    val mfaMethod: MfaMethod,
)


data class MfaEnrollmentAssociationRequest(
    val ticket: String,
    val code: String,
)

