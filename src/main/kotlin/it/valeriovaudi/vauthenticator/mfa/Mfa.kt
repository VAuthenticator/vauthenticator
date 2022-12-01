package it.valeriovaudi.vauthenticator.mfa


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
    fun sendMfaChallenge(mail: String)
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeFor(mail: String, challenge: MfaChallenge)
}


