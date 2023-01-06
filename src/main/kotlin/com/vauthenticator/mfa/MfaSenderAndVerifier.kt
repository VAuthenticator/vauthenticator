package com.vauthenticator.mfa

import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.mail.MailSenderService


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

class AccountAwareOtpMfaVerifier(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa
) : OtpMfaVerifier {
    override fun verifyMfaChallengeFor(mail: String, challenge: MfaChallenge) {
        val account = accountRepository.accountFor(mail).get()
        otpMfa.verify(account, challenge)
    }

}