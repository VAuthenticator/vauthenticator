package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mail.MailSenderService


class MfaException(message: String) : RuntimeException(message)

@JvmInline
value class MfaSecret(private val content: String) {
    fun content() = content
}

@JvmInline
value class MfaChallenge(private val content: String) {
    fun content() = content
}

interface OtpMfaSender {
    fun sendMfaChallenge(mail: String)
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeFor(mail: String, challenge: MfaChallenge)
}


class OtpMfaEmailSender(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaMailSender: MailSenderService
) : OtpMfaSender {
    override fun sendMfaChallenge(mail: String) {
        val account = accountRepository.accountFor(mail).get()
        val mfaSecret = otpMfa.generateSecretKeyFor(account)
        val mfaCode = otpMfa.getTOTPCode(mfaSecret).content()
        mfaMailSender.sendFor(account, mapOf("mfaCode" to mfaCode))
    }

}