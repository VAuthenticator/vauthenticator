package com.vauthenticator.server.mfa.api

import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/mfa/enrollment")
class MfaEnrolmentAssociationEndPoint(private val mfaAccountMethodsRepository: MfaAccountMethodsRepository) {


    @GetMapping("/api/mfa/enrollment")
    fun findAllAssociatedEnrolledMfaMethods(authentication: Authentication) =
        ok(
            mfaAccountMethodsRepository.findAll(authentication.name)
                .map { MfaAccountEnrolledMethodRepresentation(it.email, it.method.name) }
        )

}

data class MfaAccountEnrolledMethodRepresentation(val email: String, val mfaMethod: String)
