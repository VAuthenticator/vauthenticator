package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.keys.Kid
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

data class MfaDevice(val userName: String, val mfaMethod: MfaMethod, val mfaChannel: String)

data class MfaAssociationId(val content: String) {

    companion object {
        fun from(mfaDevice: MfaDevice): MfaAssociationId =
            MfaAssociationId(
                encoder.encodeToString("${mfaDevice.userName} \t ${mfaDevice.mfaMethod.name} \t ${mfaDevice.mfaChannel}".toByteArray())
            )

    }

}

class MfaFailureEvent(authentication: Authentication, exception: AuthenticationException) :
    AbstractAuthenticationFailureEvent(authentication, exception)

class MfaSuccessEvent(authentication: Authentication) : AbstractAuthenticationEvent(authentication) {}

@JvmInline
value class MfaSecret(private val content: String) {
    fun content() = content
}

@JvmInline
value class MfaChallenge(private val content: String) {
    fun content() = content
}

enum class MfaMethod { EMAIL_MFA_METHOD, SMS_MFA_METHOD, OTP_MFA_METHOD }
data class MfaAccountMethod(
    val userName: String,
    val key: Kid,
    val method: MfaMethod,
    val mfaChannel: String,
    val associated: Boolean
)

class MfaException(message: String) : AuthenticationException(message)

class UnAssociatedMfaVerificationException(message: String) : AuthenticationException(message)
class AssociatedMfaVerificationException(message: String) : AuthenticationException(message)
