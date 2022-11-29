package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.account.Account


class MfaException(message:String) : RuntimeException(message)

@JvmInline
value class MfaSecret(private val content : String) {
    fun content() = content
}

@JvmInline
value class MfaChallenge(private val content : String) {
    fun content() = content
}

interface OtpMfaSender {
    fun sendMfaChallenge(account : Account): MfaSecret
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeFor(account : Account, challenge: MfaChallenge)
}


