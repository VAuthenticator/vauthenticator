package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.keypair.Kid


class MfaException(message: String) : RuntimeException(message)

@JvmInline
value class MfaSecret(private val content: String) {
    fun content() = content
}

@JvmInline
value class MfaChallenge(private val content: String) {
    fun content() = content
}

enum class MfaMethod { EMAIL_MFA_METHOD }

data class MfaAccountMethod(val email: String, val key: Kid, val method: MfaMethod)