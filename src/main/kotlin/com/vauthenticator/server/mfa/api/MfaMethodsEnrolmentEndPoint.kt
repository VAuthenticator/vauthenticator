package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.extentions.clientAppId
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.oauth2.clientapp.domain.Scope
import com.vauthenticator.server.oauth2.clientapp.domain.Scopes
import com.vauthenticator.server.role.PermissionValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

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
                    MfaDevice(
                        sensitiveEmailMasker.mask(it.userName),
                        it.method,
                        sensitiveEmailMasker.mask(it.mfaChannel),
                        it.key.content()
                    )
                }
        )

    @PutMapping("/api/mfa/device")
    fun setDefaultEnrolledMfaMethods(
        authentication: Authentication,
        @RequestBody defaultMethod: SetDefaultMfaDeviceRequest
    ) =
        ok(
            mfaAccountMethodsRepository.setAsDefault(
                authentication.name,
                MfaDeviceId(defaultMethod.deviceId)
            )
        )


    @PostMapping("/api/mfa/enrollment")
    fun enrollMfa(
        authentication: JwtAuthenticationToken,
        @RequestBody enrolling: MfaEnrollmentRequest
    ): ResponseEntity<MfaEnrollmentResponse> {
        permissionValidator.validate(authentication, Scopes.from(Scope.MFA_ENROLLMENT))
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
        authentication: JwtAuthenticationToken,
        @RequestBody associationRequest: MfaEnrollmentAssociationRequest,
    ): ResponseEntity<Unit> {
        permissionValidator.validate(authentication, Scopes.from(Scope.MFA_ENROLLMENT))
        mfaMethodsEnrolmentAssociation.associate(associationRequest.ticket, associationRequest.code)
        return noContent().build()
    }

}

data class SetDefaultMfaDeviceRequest(
    val deviceId: String
)

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

