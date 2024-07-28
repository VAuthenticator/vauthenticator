package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.extentions.clientAppId
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.EmailMfaDevice
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollment
import com.vauthenticator.server.mfa.domain.MfaMethodsEnrollmentAssociation
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.role.PermissionValidator
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MfaEnrolmentAssociationEndPoint(
    private val sensitiveEmailMasker: SensitiveEmailMasker,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val mfaMethodsEnrollment: MfaMethodsEnrollment,
    private val mfaMethodsEnrolmentAssociation: MfaMethodsEnrollmentAssociation,
    private val permissionValidator: PermissionValidator
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
        authentication: JwtAuthenticationToken,
        httpSession: HttpSession,
        @RequestBody enrolling: MfaEnrollmentRequest
    ): ResponseEntity<MfaEnrollmentResponse> {
        permissionValidator.validate(authentication, httpSession, Scopes.from(Scope.MFA_ENROLLMENT))
        val ticketId = mfaMethodsEnrollment.enroll(
            authentication.name,
            enrolling.mfaMethod,
            enrolling.mfaChannel,
            authentication.clientAppId(),
            true
        )
        return status(HttpStatus.CREATED).body(MfaEnrollmentResponse(ticketId.content))
    }

    @PostMapping("/api/mfa/associate")
    fun associateMfaEnrollment(
        httpSession: HttpSession,
        authentication: JwtAuthenticationToken,
        @RequestBody associationRequest: MfaEnrollmentAssociationRequest,
    ): ResponseEntity<Unit> {
        permissionValidator.validate(authentication, httpSession, Scopes.from(Scope.MFA_ENROLLMENT))
        mfaMethodsEnrolmentAssociation.associate(associationRequest.ticket, associationRequest.code)
        return ResponseEntity.noContent().build()
    }

}

data class MfaEnrollmentRequest(
    val mfaChannel: String,
    val mfaMethod: MfaMethod,
)

data class MfaEnrollmentResponse(
    val ticket: String
)

data class MfaEnrollmentAssociationRequest(
    val ticket: String,
    val code: String,
)

