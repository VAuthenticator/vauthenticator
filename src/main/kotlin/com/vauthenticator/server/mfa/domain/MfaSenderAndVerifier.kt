package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService


interface OtpMfaSender {
    fun sendMfaChallenge(email: String)
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeFor(email: String, challenge: MfaChallenge)
}

class OtpMfaEmailSender(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaMailSender: EMailSenderService
) : OtpMfaSender {

    override fun sendMfaChallenge(email: String) {
        val account = accountRepository.accountFor(email).get()
        val mfaSecret = otpMfa.generateSecretKeyFor(account)
        val mfaCode = otpMfa.getTOTPCode(mfaSecret).content()
        mfaMailSender.sendFor(account, mapOf("mfaCode" to mfaCode))
    }
}

class AccountAwareOtpMfaVerifier(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa
) : OtpMfaVerifier {
    override fun verifyMfaChallengeFor(email: String, challenge: MfaChallenge) {
        val account = accountRepository.accountFor(email).get()
        otpMfa.verify(account, challenge)
    }

}