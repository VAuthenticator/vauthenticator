package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.EMailSenderService


interface OtpMfaSender {
    fun sendMfaChallenge(userName: String, challengeChannel: String)
}

interface OtpMfaVerifier {
    fun verifyMfaChallengeFor(userName: String, challenge: MfaChallenge)
}

class OtpMfaEmailSender(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa,
    private val mfaMailSender: EMailSenderService
) : OtpMfaSender {

    override fun sendMfaChallenge(userName: String, challengeChannel: String) {
        val account = accountRepository.accountFor(userName).get()
        val mfaSecret = otpMfa.generateSecretKeyFor(account)
        val mfaCode = otpMfa.getTOTPCode(mfaSecret).content()
        mfaMailSender.sendFor(account, mapOf("email" to challengeChannel, "mfaCode" to mfaCode))
    }
}

class AccountAwareOtpMfaVerifier(
    private val accountRepository: AccountRepository,
    private val otpMfa: OtpMfa
) : OtpMfaVerifier {
    override fun verifyMfaChallengeFor(userName: String, challenge: MfaChallenge) {
        val account = accountRepository.accountFor(userName).get()
        otpMfa.verify(account, challenge)
    }

}